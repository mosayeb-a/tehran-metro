# Tehran Metro

**Tehran Metro** is an offline Android application designed to help users navigate the Tehran Metro system. The app provides key features to make commuting easier, such as viewing station details, finding the shortest path between stations, and more.

<img src="https://github.com/user-attachments/assets/bc8e7919-4bea-4c67-803a-6897013668c6" width="150" style="display: inline-block; margin-right: 10px;"/>
<img src="https://github.com/user-attachments/assets/36ebec47-146a-4833-88be-dd231a3b57e9" width="150" style="display: inline-block; margin-right: 10px;"/>
<img src="https://github.com/user-attachments/assets/3b37e083-c3a0-4fe5-a33d-d7e3d96ed787" width="150" style="display: inline-block; margin-right: 10px;"/>
<img src="https://github.com/user-attachments/assets/99f41c03-0ea4-4900-b6c0-72e1100eed0f" width="150" style="display: inline-block; margin-right: 10px;"/>
<img src="https://github.com/user-attachments/assets/d462372d-7722-431a-afd2-37def1fe9cd0" width="150" style="display: inline-block; margin-right: 10px;"/>
<img src="https://github.com/user-attachments/assets/1f0d51aa-542e-44ab-a8bf-cf1661987c61" width="150" style="display: inline-block; margin-right: 10px;"/>

## Features
- **View Stations by Line**:  
  Easily explore all stations categorized by their respective metro lines.

- **Station Details**:  
  Get detailed information about each station, including available facilities, address, and more.

- **Find Shortest Path**:  
  Calculate the shortest path between two stations using efficient algorithms like Dijkstra's algorithm.

the project is developed using **compose** for building UIs and implements algorithms for optimized route calculations.

## Contribute
1. **Bug Fixes and Improvements**: Feel free to submit a pull request for bug fixes, UI/UX improvements, or other enhancements.
2. **New Features**: 
   - Before implementing a new feature, **open an issue** to discuss it with the maintainers.
   - If you're interested in building a feature from the **Planned Features** list, first discuss it in the corresponding issue before starting implementation.

## Planned Features
1. **Add Branches to Line 1 and 4**
2. ~~**Find Nearest Station by Current Location**~~ (Done)
3. ~~**Show All Stations on the Map**~~ (Done)
5. **Time Estimate for Shortest Path**: Calculate the estimated travel time between stations, considering line changes and station count.
6. ~~**A way to edit station info by users or report any issue to remote**~~ (Done)
7. **Refactor GMS and Firebase usage to comply with F-Droid's open-source policies**
8. **Add metro train timetable: get the official Tehran Metro schedule from XLSX files and convert it into JSON format.**


If you'd like to work on any of these features, please open an issue to discuss your approach before starting implementation.

## Data Source
The station data is sourced from a JSON file compiled by **Mostafa**. You can find the data [here](https://github.com/mostafa-kheibary/tehran-metro-data/).

## License
Tehran Metro is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 or any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; to the extent permitted by law. See the [GNU General Public License](https://www.gnu.org/licenses/) for details.
