# Maps

## Description
This app uses the ArcGIS Runtime SDK that works with preplanned map areas.


## Features
- The first screen shows a list of maps (MapListScreen.kt) - the webmap and the preplanned map areas that have been created.

![first_screen.png](..%2F..%2F..%2Ffirst_screen.png)

The webmap, Explore Maine, with the id 3bc3179f17da44a0ac0bfdac4ad15664 is hosted on ArcGIS Online and is publicly available.
https://www.arcgis.com/home/item.html?id=3bc3179f17da44a0ac0bfdac4ad15664

- When the user taps on the web map, the app displays it as a new screen (MapDetailScreen.kt) and includes a button to go back to the initial screen.

![Web_Map_Detail.png](..%2F..%2F..%2FWeb_Map_Detail.png)

- Preplanned map areas must be downloaded before they can be opened. There is download button that downloads the map area. While the download is happening, a circular progress indicator is shown. 

![Map_downloading.png](..%2F..%2F..%2FMap_downloading.png)
Once the map is downloaded, the download button is replaced with an options menu to either view or delete the downloaded map.
![options menu.png](..%2F..%2F..%2Foptions%20menu.png)

- Once a map area has been downloaded, it can be opened. Tapping on a downloaded map area or clicking on the view map option in the options menu opens the map.
![offline map detail.png](..%2F..%2F..%2Foffline%20map%20detail.png)

- If the app starts with no network connection, a list of any previously downloaded map areas is shown to the user. The user can then open any of the downloaded map areas.

![downloded offline map list.png](..%2F..%2F..%2Fdownloded%20offline%20map%20list.png)