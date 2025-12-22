import com.ma.tehro.data.PositionInLine
import com.ma.tehro.data.Station
import com.ma.tehro.data.Translations
import com.ma.tehro.data.repo.PathRepositoryImpl
import com.ma.tehro.domain.PathItem
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ShortestPathTest {

    private lateinit var repository: PathRepositoryImpl

    // line1: A > B > C > D > X
    // line2: C > E > F
    // line3: B > G > H
    private fun buildGraph(): Map<String, Station> {
        val a = station("A", lines = listOf(1), pos = listOf(pos(1, 1)), rel = listOf("B"))
        val b = station(
            "B",
            lines = listOf(1, 3),
            pos = listOf(pos(1, 2), pos(3, 1)),
            rel = listOf("A", "C", "G")
        )
        val c = station(
            "C",
            lines = listOf(1, 2),
            pos = listOf(pos(1, 3), pos(2, 1)),
            rel = listOf("B", "D", "E")
        )
        val d = station("D", lines = listOf(1), pos = listOf(pos(1, 4)), rel = listOf("C", "X"))
        val x = station("X", lines = listOf(1), pos = listOf(pos(1, 5)), rel = listOf("D"))
        val e = station("E", lines = listOf(2), pos = listOf(pos(2, 2)), rel = listOf("C", "F"))
        val f = station("F", lines = listOf(2), pos = listOf(pos(2, 3)), rel = listOf("E"))
        val g = station("G", lines = listOf(3), pos = listOf(pos(3, 2)), rel = listOf("B", "H"))
        val h = station("H", lines = listOf(3), pos = listOf(pos(3, 3)), rel = listOf("G"))

        return mapOf(
            "A" to a, "B" to b, "C" to c, "D" to d, "X" to x,
            "E" to e, "F" to f, "G" to g, "H" to h
        )
    }

    @BeforeTest
    fun setup() {
        val stations = buildGraph()
        repository = PathRepositoryImpl(stations)
    }

    @Test
    fun `direct path on same line - no line change`() = runTest {
        val result = repository.findShortestPathWithDirection("A", "X")

        val stations = result.filterIsInstance<PathItem.StationItem>().map { it.station.name }
        assertEquals(listOf("A", "B", "C", "D", "X"), stations)

        val titles = result.filterIsInstance<PathItem.Title>()
        assertEquals(1, titles.size)
        assertTrue(titles[0].en.contains("Line 1"))
    }

    @Test
    fun `path with one line change - prefers fewer stations even with change`() = runTest {
        val result = repository.findShortestPathWithDirection("A", "F")

        val stations = result.filterIsInstance<PathItem.StationItem>().map { it.station.name }
        assertEquals(listOf("A", "B", "C", "C", "E", "F"), stations) // change at C

        val titles = result.filterIsInstance<PathItem.Title>()
        assertEquals(2, titles.size)
        assertTrue(titles[0].en.contains("Line 1"))
        assertTrue(titles[1].en.contains("Line 2"))
    }

    @Test
    fun `high line change cost prefers longer same-line path`() = runTest {
        val extended = buildGraph().toMutableMap()
        val fUpdated = extended["F"]!!.copy(
            lines = listOf(1, 2),
            positionsInLine = listOf(PositionInLine(6, 1), PositionInLine(3, 2)),
            relations = listOf("E", "Y")
        )
        val y = station("Y", lines = listOf(1), pos = listOf(pos(1, 7)), rel = listOf("F"))
        extended += mapOf("F" to fUpdated, "Y" to y)
        extended["X"] = extended["X"]!!.copy(relations = listOf("D", "Y"))

        val repo = PathRepositoryImpl(extended)

        val rawPath = repo.findShortestPath(extended, "A", "F", lineChangeCost = 20).path
        assertEquals(listOf("A", "B", "C", "D", "X", "Y", "F"), rawPath)
    }

    @Test
    fun `disabled station is marked as passthrough`() = runTest {
        val graph = buildGraph().toMutableMap()
        graph["C"] = graph["C"]!!.copy(disabled = true)

        val repo = PathRepositoryImpl(graph)
        val result = repo.findShortestPathWithDirection("A", "X")

        val cItem = result.filterIsInstance<PathItem.StationItem>()
            .find { it.station.name == "C" }

        assertEquals(cItem?.isPassthrough, true)
    }

    @Test
    fun `direction title correct - forward on line`() = runTest {
        val result = repository.findShortestPathWithDirection("A", "X")

        val firstTitle = result.filterIsInstance<PathItem.Title>().first()
        assertTrue(firstTitle.en.contains("Line 1"))
    }

    @Test
    fun `direction title correct - backward on line`() = runTest {
        val result = repository.findShortestPathWithDirection("X", "A")

        val stations = result.filterIsInstance<PathItem.StationItem>().map { it.station.name }
        assertEquals(listOf("X", "D", "C", "B", "A"), stations)

        val firstTitle = result.filterIsInstance<PathItem.Title>().first()
        assertTrue(firstTitle.en.contains("Line 1"))
    }

    @Test
    fun `no path returns empty list`() = runTest {
        val result = repository.findShortestPathWithDirection("A", "NONEXISTENT")
        assertTrue(result.isEmpty())
    }

    private fun station(
        name: String,
        lines: List<Int>,
        pos: List<PositionInLine>,
        rel: List<String>,
        disabled: Boolean = false
    ) = Station(
        name = name,
        translations = Translations(fa = name),
        lines = lines,
        positionsInLine = pos,
        relations = rel,
        disabled = disabled
    )

    private fun pos(line: Int, position: Int) = PositionInLine(position, line)
}