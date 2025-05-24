# Price-Comparator Market

## Project Description
Price-Comparator Market is an application used by users to compare prices of many products across different supermarkets

## Technologies Used
- **Spring Boot**
- **PostgreSQL**

## Testing
- **Postman**
- **debugger**
- **System.out.print()**

## Setup
Copy the project's SSH and clone it locally

### .env file
In order for the project to work, we need to first configure the .env file\
Go to src/main/resources -> rename the .env.example file to .env -> fill in the variables with the information requested 

### Starting the application
Go to PriceComparatorMarketApp.class and run the application

### Simulate date
You can change the date in the SimulateDate helper class for different results

## Project Structure
I decided to separate the code in 4 big parts:
1. Controllers: used to expose service methods to the API end points
2. Services: used for the business logic
3. Repositories: used as a way to access, manage and manipulate the data from the database
4. Models:
  - DTOs: used for choosing what data to expose to the API
  - Representations: as the name says, these classes will represent the data that comes from the CSV files
  - The rest of the classes: used as structures for the database entity

## Assumptions
- For the Custom Price Alert I decided to go with a notification system. A user can choose one product and a price for it and when that product gets a lower price or equal to the desired price, it will create a notification in the database which will be seen by the user whenever he visits the application/site again.
- For the Dynamic Price History Graphs, I had the idea in my head, but it got very complicated and complex, which might be due to me either understanding the task incorrectly or the way I choose to store the prices and discounts a supermarket had. I choose to only filter the products by brand, category and supermarket name, which is not much of what was asked
- I made the assumption that the date from the csv file is the publish date of the supermarkets/products, not to the database, but at that date, the supermarkets have those products at that price 

## How to use the implemented features
I recommand using Postman; I attached bellow a postman link with all the requests available. 
  - For the csv files, at body, choose form-data and name the key "file" as well as choosing File from the dropdown
  - For the end points that require PathVariables, make sure the ids relate to the entity in the database
I also attached a video showcasing all the endpoints being used

### Video showcase
https://streamable.com/m3oi7l

### Postman requests
https://solar-firefly-60818.postman.co/workspace/TravelTeamWorkspace~71f58a90-732d-4494-9707-2e702429e26a/collection/26083537-53e647b4-190b-4a7f-b726-9c107ae8fc39?action=share&creator=26083537
