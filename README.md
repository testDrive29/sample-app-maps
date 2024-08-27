# Maps

## Description
This app uses the ArcGIS Runtime SDK that works with preplanned map areas.


## Features
- The first screen shows a list of maps (MapListScreen.kt) - the webmap and the preplanned map areas that have been created.
<img src="https://github.com/user-attachments/assets/2d04ba96-9285-4ff9-916d-2223815fbec4" height="600" width="300">


The webmap, Explore Maine, with the id 3bc3179f17da44a0ac0bfdac4ad15664 is hosted on ArcGIS Online and is publicly available.
https://www.arcgis.com/home/item.html?id=3bc3179f17da44a0ac0bfdac4ad15664

- When the user taps on the web map, the app displays it as a new screen (MapDetailScreen.kt) and includes a button to go back to the initial screen.
<img src="https://github.com/user-attachments/assets/6d46e045-79d9-42bb-a209-2e050d7451c5" height="600" width="300">


- Preplanned map areas must be downloaded before they can be opened. There is download button that downloads the map area. While the download is happening, a circular progress indicator is shown. 
<img src="https://github.com/user-attachments/assets/5d8c5cb1-9c83-46d5-a22e-ef1634381210" height="600" width="300">


- Once the map is downloaded, the download button is replaced with an options menu to either view or delete the downloaded map.
<img src="https://github.com/user-attachments/assets/ee47f789-faa2-4f1f-840c-4c5ae6529295" height="600" width="300">


- Once a map area has been downloaded, it can be opened. Tapping on a downloaded map area or clicking on the view map option in the options menu opens the map.
<img src="https://github.com/user-attachments/assets/72229808-95dc-4f54-a822-1f72399b44e2" height="600" width="300">


- If the app starts with no network connection, a list of any previously downloaded map areas is shown to the user. The user can then open any of the downloaded map areas.
<img src="https://github.com/user-attachments/assets/6e694d3a-2659-4083-b5bb-5e32008b9a6a" height="600" width="300">

