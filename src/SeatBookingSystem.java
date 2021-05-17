import com.mongodb.client.*;
import com.mongodb.BasicDBObject;
import org.bson.Document;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.text.Font;
import javafx.scene.shape.Rectangle;

import java.util.*;
import java.util.List;
import java.time.LocalDate;

public class SeatBookingSystem extends Application {
    private static final int SEATING_CAPACITY = 42; //declare a global constant variable for seating capacity

    public static void main(String[] args) {
        Application.launch();
    }

    public void start(Stage primaryStage) {
        HashMap<String,String> customerDetails = new HashMap<>(); //hash map key:customer id value:customer name
        HashMap<String,List<String>> bookedSeatsDetails = new HashMap<>(); //hash map key:seat reference number value:array list[date path reference,from,to,customer id,seat number]

        loop: //loop until user enter 'Q' or 'q' to quit program
        while (true) { //call appropriate method based on the user input
            switch (viewMenu()) {
                case "A":
                case "a":
                    addCustomer(customerDetails,bookedSeatsDetails);  //call addCustomer() method
                    break;
                case "V":
                case "v":
                    viewAllSeats(bookedSeatsDetails);  //call viewAllSeats() method
                    break;
                case "E":
                case "e":
                    viewEmptySeats(bookedSeatsDetails);  //call viewEmptySeats() method
                    break;
                case "D":
                case "d":
                    deleteCustomer(customerDetails,bookedSeatsDetails);  //call deleteCustomer() method
                    break;
                case "F":
                case "f":
                    findSeatByCustomerName(customerDetails,bookedSeatsDetails);  //call findSeatByCustomerName() method
                    break;
                case "S":
                case "s":
                    storeDataIntoFile(customerDetails,bookedSeatsDetails);  //call storeDataIntoFile() method
                    break;
                case "L":
                case "l":
                    loadDataFromFile(customerDetails,bookedSeatsDetails);  //call loadDataFromFile() method
                    break;
                case "O":
                case "o":
                    viewSeatsOrderedAlphabeticallyByName(customerDetails,bookedSeatsDetails);  //call viewSeatsOrderedAlphabeticallyByName() method
                    break;
                case "Q":  //allow user to quit program
                case "q":
                    System.out.println("Before exiting from the program, make sure you have stored all the data into the file using 'S' option in the menu.");
                    Scanner input = new Scanner(System.in);
                    System.out.println("Do you want to quit program (Y/N) ?");
                    String quit = input.nextLine().toUpperCase();
                    while (!quit.equals("Y") && !quit.equals("N")) {
                        System.out.println("Please enter either 'Y' or 'N' as your answer");
                        quit = input.nextLine().toUpperCase();
                    }
                    if(quit.equals("Y")) {
                        System.out.println("Thank you for using our seat booking system.Now you are exiting from the program.");
                        break loop;
                    }
                    break;
                default:  //execute if there`s no case match
                    System.out.println("Sorry! You have entered an invalid input.Please try again");
            }
        }
    }

    public static String viewMenu() { //view menu with available options and let user to select an option
        System.out.println();
        System.out.println("Welcome to the train seat booking program!");
        System.out.println("From here, you can book seats in A/C compartment of \"Denuwara Menike\" train");
        System.out.println();
        ArrayList<String> menuOptions = new ArrayList<>(Arrays.asList("----------Menu----------", "A - Add a customer to a seat", "V - View all seats", "E - Display empty seats",
                "D - Delete customer from a seat", "F - Find the seat for a given customer name", "S - Store booking details into file", "L - Load booking details from the file",
                "O - View seats ordered alphabetically by customer name", "Q - Quit Program"));
        for(String i : menuOptions) {
            System.out.println(i);
        }
        System.out.println();
        Scanner option = new Scanner(System.in);
        System.out.println("Please enter the relevant letter of the option you want to select : ");
        return option.nextLine(); //return user input
    }

    public static void addCustomer(HashMap<String,String> customerDetailsList, HashMap<String,List<String>> seatDetailsList) { //let user to add a customer to a seat for a specified date
        ArrayList<String> customerJourneyData = new ArrayList<>();
        ArrayList<String> checkOutOfSeat = new ArrayList<>();

        //create GUI to get departure date,path,from and to values from user
        GridPane gridlayout1 = new GridPane();   //insert a grid pane
        gridlayout1.setPadding(new Insets(20, 20, 20, 20));
        gridlayout1.setVgap(15);
        gridlayout1.setHgap(15);

        Label description = new Label("Please fill these details inorder to book a seat"); //create labels,buttons and other elements
        description.setFont(new Font("Arial Rounded MT Bold", 22));
        Label departureDateLabel = new Label("Departure Date: ");
        DatePicker departureDate = new DatePicker();
        departureDate.setDayCellFactory(param -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate currentDate = LocalDate.now();
                setDisable(empty || date.compareTo(currentDate) < 0);
            }
        });
        Label selectPathLabel = new Label("Select your path");
        ObservableList<String> pathOptions = FXCollections.observableArrayList("Colombo to Badulla", "Badulla to Colombo");
        ChoiceBox<String> path = new ChoiceBox<>(pathOptions);
        ObservableList<String> fromAndToOptions = FXCollections.observableArrayList("Colombo","Peradeniya", "Nanuoya", "Badulla");
        Label startingPointLabel = new Label("From: ");
        ChoiceBox<String> pathStartingPoint = new ChoiceBox<>(fromAndToOptions);
        Label endingPointLabel = new Label("To: ");
        ChoiceBox<String> pathEndingPoint = new ChoiceBox<>(fromAndToOptions);
        Button OKButton = new Button("   OK   ");

        gridlayout1.add(description, 8, 3, 10, 1); //add above created elements into the grid pane
        gridlayout1.add(departureDateLabel, 6, 6, 3, 1);
        gridlayout1.add(departureDate, 9, 6, 3, 1);
        gridlayout1.add(selectPathLabel, 6, 8, 3, 1);
        gridlayout1.add(path, 9, 8, 3, 1);
        gridlayout1.add(startingPointLabel, 6, 9, 3, 1);
        gridlayout1.add(pathStartingPoint, 9, 9, 3, 1);
        gridlayout1.add(endingPointLabel, 6, 10, 3, 1);
        gridlayout1.add(pathEndingPoint, 9, 10, 3, 1);
        gridlayout1.add(OKButton, 20, 13, 5, 1);

        OKButton.setOnAction(event -> {
            LocalDate date = departureDate.getValue(); //get user inputs and put them into variables
            String pathValue = path.getValue();
            String from = pathStartingPoint.getValue();
            String to = pathEndingPoint.getValue();

            if ((date == null) || (pathValue == null) || (from == null) || (to == null)) { //check whether the user has filled all the input fields
                Alert emptyDataAlert = new Alert(Alert.AlertType.WARNING);
                emptyDataAlert.setTitle("Fill Data Alert");
                emptyDataAlert.setContentText("Please fill all the required data fields inorder to book a seat.");
                emptyDataAlert.showAndWait();
            } else {
                customerJourneyData.add(date + pathValue);
                customerJourneyData.add(from);
                customerJourneyData.add(to);

                Stage currentStage = (Stage) OKButton.getScene().getWindow();
                currentStage.close();     //close current GUI
            }
        });

        Scene scene1 = new Scene(gridlayout1, 1000, 500);
        Stage primaryStage1 = new Stage();
        primaryStage1.setTitle("Train Seat Booking System - Add a customer - Customer Journey Details");
        primaryStage1.setScene(scene1);
        primaryStage1.showAndWait();

        if (customerJourneyData.size() == 3) { //check whether the customer has provided all the required data
            GridPane gridlayout2 = new GridPane(); //create GUI to show seats in the train compartment
            gridlayout2.setPadding(new Insets(20, 20, 20, 20));
            gridlayout2.setVgap(15);
            gridlayout2.setHgap(15);

            String datePathReference = customerJourneyData.get(0);
            String dateSubstring = datePathReference.substring(0,10);
            String from = customerJourneyData.get(1);
            String to = customerJourneyData.get(2);

            int xPosition = 6, yPosition = 6;
            ArrayList<Integer> bookedSeats = new ArrayList<>();
            for (int i = 1; i <= SEATING_CAPACITY; i++) {
                Button seatButton = new Button("Seat " + i);
                seatButton.setPrefSize(170, 130);
                seatButton.setFont(new Font(18));
                seatButton.setStyle("-fx-background-color:blue;-fx-text-fill:white");
                gridlayout2.add(seatButton, xPosition, yPosition);
                String seatReference = i + datePathReference;
                for (String seatReferenceNumber : seatDetailsList.keySet()) {
                    if (seatReferenceNumber.equals(seatReference)) {   //check whether the seat has booked before
                        seatButton.setDisable(true);
                        seatButton.setStyle("-fx-background-color:red;-fx-text-fill:white");
                    }
                }
                if (i % 3 == 0 && i % 6 != 0) {   //set positions of the seat buttons
                    xPosition = 15;
                } else if (i % 6 == 0) {
                    xPosition = 6;
                    yPosition += 1;
                } else {
                    xPosition += 1;
                }
                int seatNumber = i;
                seatButton.setOnAction(event -> {
                    seatButton.setStyle("-fx-background-color:red;-fx-text-fill:white");
                    if (!bookedSeats.contains(seatNumber)) {
                        bookedSeats.add(seatNumber);
                    }
                });

                for (String seatReferenceNumber : seatDetailsList.keySet()) {
                    if (seatReference.equals(seatReferenceNumber)) {
                        checkOutOfSeat.add(seatReference);
                    }
                }
            }

            Label descriptionLabel = new Label("Here`s a view of how seats are arranged in the A/C compartment of 'Denuwara Menike' train");
            descriptionLabel.setFont(new Font("Arial Rounded MT Bold", 22));
            Rectangle blueRectangle = new Rectangle(70, 50);
            blueRectangle.setStyle("-fx-fill:blue");
            Label availableSeats = new Label("Available Seats");
            Rectangle redRectangle = new Rectangle(70, 50);
            redRectangle.setStyle("-fx-fill:red");
            Label reservedSeats = new Label("Reserved Seats");
            Label dateLabel = new Label("Departure Date:   " + dateSubstring);
            Label startingPoint = new Label("From:   " + from);
            Label endingPoint = new Label("To:   " + to);
            Label customerNameLabel = new Label("Customer Name: ");
            TextField customerName = new TextField();
            Label customerIDLabel = new Label("Customer NIC number: ");
            TextField customerID = new TextField();
            Button confirmButton = new Button("Confirm Booking");

            gridlayout2.add(descriptionLabel, 6, 1, 12, 1);
            gridlayout2.add(blueRectangle, 20, 1);
            gridlayout2.add(availableSeats, 21, 1, 3, 1);
            gridlayout2.add(redRectangle, 24, 1);
            gridlayout2.add(reservedSeats, 25, 1, 3, 1);
            gridlayout2.add(dateLabel, 20, 6, 3, 1);
            gridlayout2.add(startingPoint, 20, 7, 2, 1);
            gridlayout2.add(endingPoint, 25, 7, 2, 1);
            gridlayout2.add(customerNameLabel, 20, 13, 3, 1);
            gridlayout2.add(customerName, 23, 13, 3, 1);
            gridlayout2.add(customerIDLabel, 20, 14, 3, 1);
            gridlayout2.add(customerID, 23, 14, 3, 1);
            gridlayout2.add(confirmButton, 20, 16, 10, 1);

            if (checkOutOfSeat.size() == SEATING_CAPACITY) { //check whether all the seats have been booked before
                Alert outOfSeatsAlert = new Alert(Alert.AlertType.WARNING);
                outOfSeatsAlert.setTitle("Out Of Seats Alert");
                outOfSeatsAlert.setContentText("Sorry, all the seats have been booked.There are no seats left.");
                outOfSeatsAlert.showAndWait();
                confirmButton.setDisable(true);
            }

            confirmButton.setOnAction(event -> {
                if (bookedSeats.size() != 0) { //check whether the customer has selected any seat to book
                    String formattedCustomerName = customerName.getText().toLowerCase();
                    String formattedIDNumber = customerID.getText().toLowerCase();
                    if (!formattedCustomerName.equals("") && !formattedIDNumber.equals("")) {
                        if (customerDetailsList.containsKey(formattedIDNumber) && !customerDetailsList.get(formattedIDNumber).equals(formattedCustomerName)) {
                            Alert duplicateIDAlert = new Alert(Alert.AlertType.WARNING); //alert if another customer has booked seats with the same ID
                            duplicateIDAlert.setTitle("Duplicate ID Alert");
                            duplicateIDAlert.setContentText("Sorry, another customer has booked seats under this NIC number.Please re-check whether you have entered the correct NIC number.");
                            duplicateIDAlert.showAndWait();
                        } else {
                            customerDetailsList.put(formattedIDNumber, formattedCustomerName); //add user inputs into the hash map
                            for (Integer bookedSeat : bookedSeats) {
                                ArrayList<String> seatDataList = new ArrayList<>();
                                seatDataList.add(datePathReference);
                                seatDataList.add(from);
                                seatDataList.add(to);
                                seatDataList.add(formattedIDNumber);
                                seatDataList.add(bookedSeat.toString());
                                String seatReference = bookedSeat + datePathReference;
                                seatDetailsList.put(seatReference, seatDataList);
                            }
                            Stage currentStage = (Stage) confirmButton.getScene().getWindow();
                            currentStage.close();
                            System.out.println(formattedCustomerName + ", you have successfully booked following seats: ");
                            Collections.sort(bookedSeats);
                            for (Integer y : bookedSeats) {
                                System.out.println("       Seat " + y);
                            }
                        }
                    } else {
                        Alert fillDataAlert = new Alert(Alert.AlertType.WARNING); //alert if required data hasn`t filled
                        fillDataAlert.setTitle("Fill Data Alert");
                        fillDataAlert.setContentText("Please fill customer`s name and NIC number to confirm booking.");
                        fillDataAlert.showAndWait();
                    }
                } else {
                    Alert selectSeatAlert = new Alert(Alert.AlertType.WARNING); //alert if user has requested to book seats without selecting any seat
                    selectSeatAlert.setTitle("Select A Seat Alert");
                    selectSeatAlert.setContentText("You haven`t selected any seats.Please select the seat you want to book.");
                    selectSeatAlert.showAndWait();
                }
            });

            Scene scene2 = new Scene(gridlayout2, 3000, 1000);
            Stage primaryStage2 = new Stage();
            primaryStage2.setTitle("Train Seat Booking System - Add a customer");
            primaryStage2.setScene(scene2);
            primaryStage2.showAndWait();
        }
    }

    public static void viewAllSeats(HashMap<String,List<String>> seatDetailsList) { //view all the seats for a specific date
        List<String> customerJourneyData = new ArrayList<>();

        GridPane gridlayout1 = new GridPane(); //create GUI to get departure date and path values from user
        gridlayout1.setPadding(new Insets(20, 20, 20, 20));
        gridlayout1.setVgap(15);
        gridlayout1.setHgap(15);

        Label description = new Label("Please fill these details inorder to view all seats"); //create labels,buttons and other elements
        description.setFont(new Font("Arial Rounded MT Bold", 22));
        Label departureDateLabel = new Label("Departure Date: ");
        DatePicker departureDate = new DatePicker();
        departureDate.setDayCellFactory(param -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate currentDate = LocalDate.now();
                setDisable(empty || date.compareTo(currentDate) < 0);
            }
        });
        Label selectPathLabel = new Label("Select path");
        ObservableList<String> pathOptions = FXCollections.observableArrayList("Colombo to Badulla", "Badulla to Colombo");
        ChoiceBox<String> path = new ChoiceBox<>(pathOptions);
        Button OKButton = new Button("   OK   ");

        gridlayout1.add(description, 8, 3, 10, 1); //add above created elements into the grid pane
        gridlayout1.add(departureDateLabel, 6, 6, 3, 1);
        gridlayout1.add(departureDate, 9, 6, 3, 1);
        gridlayout1.add(selectPathLabel, 6, 8, 3, 1);
        gridlayout1.add(path, 9, 8, 3, 1);
        gridlayout1.add(OKButton, 20, 13, 5, 1);

        OKButton.setOnAction(event -> {
            LocalDate date = departureDate.getValue(); //get user inputs and put them into variables
            String pathValue = path.getValue();

            if ((date == null) || (pathValue == null)) { //check whether the user has filled all the input fields
                Alert emptyDataAlert = new Alert(Alert.AlertType.WARNING);
                emptyDataAlert.setTitle("Fill Data Alert");
                emptyDataAlert.setContentText("Please fill all the required data fields inorder to view all seats.");
                emptyDataAlert.showAndWait();
            } else {
                customerJourneyData.add(date + pathValue);

                Stage currentStage = (Stage) OKButton.getScene().getWindow();
                currentStage.close();
            }
        });

        Scene scene1 = new Scene(gridlayout1, 1000, 500);
        Stage primaryStage1 = new Stage();
        primaryStage1.setTitle("Train Seat Booking System - View All Seats - Customer Journey Details");
        primaryStage1.setScene(scene1);
        primaryStage1.showAndWait();

        if (customerJourneyData.size() == 1) {
            GridPane gridlayout2 = new GridPane(); //create GUI to view all seats
            gridlayout2.setPadding(new Insets(20, 20, 20, 20));
            gridlayout2.setVgap(15);
            gridlayout2.setHgap(15);

            String datePathReference = customerJourneyData.get(0);
            String dateSubstring = datePathReference.substring(0, 10);

            int xPosition = 6, yPosition = 6;
            for (int i = 1; i <= SEATING_CAPACITY; i++) {
                Button seatButton = new Button("Seat " + i);
                seatButton.setPrefSize(170, 100);
                seatButton.setFont(new Font(18));
                seatButton.setStyle("-fx-background-color:blue;-fx-text-fill:white");
                gridlayout2.add(seatButton, xPosition, yPosition);
                String seatReference = i + datePathReference;
                for (String seatReferenceNumber : seatDetailsList.keySet()) { //check whether the seats have booked or not
                    if (seatReferenceNumber.equals(seatReference)) {
                        seatButton.setStyle("-fx-background-color:red;-fx-text-fill:white");
                    }
                }
                if (i % 3 == 0 && i % 6 != 0) { //set positions of seat buttons
                    xPosition = 20;
                } else if (i % 6 == 0) {
                    xPosition = 6;
                    yPosition += 1;
                } else {
                    xPosition += 1;
                }
            }

            Label descriptionLabel = new Label("Here`s a view of how seats are arranged in the A/C compartment of 'Denuwara Menike' train");
            descriptionLabel.setFont(new Font("Arial Rounded MT Bold", 22));
            Label dateLabel = new Label("Departure Date:   " + dateSubstring);
            Rectangle blueRectangle = new Rectangle(70, 50); //create labels,buttons and other elements
            blueRectangle.setStyle("-fx-fill:blue");
            Label availableSeats = new Label("Available Seats");
            availableSeats.setFont(new Font(18));
            Rectangle redRectangle = new Rectangle(70, 50);
            redRectangle.setStyle("-fx-fill:red");
            Label reservedSeats = new Label("Reserved Seats");
            reservedSeats.setFont(new Font(18));
            Button backToMenu = new Button("Back to Menu");

            gridlayout2.add(descriptionLabel, 6, 1, 17, 1); //add created elements into the grid pane
            gridlayout2.add(dateLabel, 34, 6, 10, 1);
            gridlayout2.add(blueRectangle, 34, 8);
            gridlayout2.add(availableSeats, 35, 8, 17, 1);
            gridlayout2.add(redRectangle, 34, 9);
            gridlayout2.add(reservedSeats, 35, 9, 17, 1);
            gridlayout2.add(backToMenu, 34, 11, 17, 1);

            backToMenu.setOnAction(event -> {
                Stage currentStage = (Stage) backToMenu.getScene().getWindow();
                currentStage.close();
            });

            Scene scene2 = new Scene(gridlayout2, 3000, 1000);
            Stage primaryStage2 = new Stage();
            primaryStage2.setTitle("Train Seat Booking System - View All Seats");
            primaryStage2.setScene(scene2);
            primaryStage2.showAndWait();
        }
    }

    public static void viewEmptySeats(HashMap<String,List<String>> seatDetailsList) { //view empty seats for a specific date
        List<String> customerJourneyData = new ArrayList<>();
        List<String> checkOutOfSeat = new ArrayList<>();

        GridPane gridlayout1 = new GridPane(); //create GUI to get departure date and path values from user
        gridlayout1.setPadding(new Insets(20, 20, 20, 20));
        gridlayout1.setVgap(15);
        gridlayout1.setHgap(15);

        Label description = new Label("Please fill these details inorder to view empty seats"); //create labels,buttons and other elements
        description.setFont(new Font("Arial Rounded MT Bold", 22));
        Label departureDateLabel = new Label("Departure Date: ");
        DatePicker departureDate = new DatePicker();
        departureDate.setDayCellFactory(param -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate currentDate = LocalDate.now();
                setDisable(empty || date.compareTo(currentDate) < 0);
            }
        });
        Label selectPathLabel = new Label("Select path");
        ObservableList<String> pathOptions = FXCollections.observableArrayList("Colombo to Badulla", "Badulla to Colombo");
        ChoiceBox<String> path = new ChoiceBox<>(pathOptions);
        Button OKButton = new Button("   OK   ");

        gridlayout1.add(description, 8, 3, 10, 1);  //add created elements into the grid pane
        gridlayout1.add(departureDateLabel, 6, 6, 3, 1);
        gridlayout1.add(departureDate, 9, 6, 3, 1);
        gridlayout1.add(selectPathLabel, 6, 8, 3, 1);
        gridlayout1.add(path, 9, 8, 3, 1);
        gridlayout1.add(OKButton, 20, 13, 5, 1);

        OKButton.setOnAction(event -> {
            LocalDate date = departureDate.getValue(); //get user inputs and put them into variables
            String pathValue = path.getValue();

            if ((date == null) || (pathValue == null)) {  //check whether user have filled all the required data
                Alert emptyDataAlert = new Alert(Alert.AlertType.WARNING);
                emptyDataAlert.setTitle("Fill Data Alert");
                emptyDataAlert.setContentText("Please fill all the required data fields inorder to view empty seats.");
                emptyDataAlert.showAndWait();
            } else {
                customerJourneyData.add(date + pathValue);

                Stage currentStage = (Stage) OKButton.getScene().getWindow();
                currentStage.close();
            }
        });

        Scene scene1 = new Scene(gridlayout1, 1000, 500);
        Stage primaryStage1 = new Stage();
        primaryStage1.setTitle("Train Seat Booking System - View Empty Seats - Customer Journey Details");
        primaryStage1.setScene(scene1);
        primaryStage1.showAndWait();

        if (customerJourneyData.size() == 1) {
            GridPane gridlayout2 = new GridPane(); //create GUI to view empty seats
            gridlayout2.setPadding(new Insets(20, 20, 20, 20));
            gridlayout2.setVgap(15);
            gridlayout2.setHgap(15);

            String datePathReference = customerJourneyData.get(0);
            String dateSubstring = datePathReference.substring(0, 10);

            int xPosition = 6, yPosition = 6;
            for (int i = 1; i <= SEATING_CAPACITY; i++) {
                String seatReference = i + datePathReference;
                if (!seatDetailsList.containsKey(seatReference)) { //create whether the seat is not booked before
                    Button seatButton = new Button("Seat " + i);
                    seatButton.setPrefSize(170, 100);
                    seatButton.setFont(new Font(18));
                    seatButton.setStyle("-fx-background-color:blue;-fx-text-fill:white");
                    gridlayout2.add(seatButton, xPosition, yPosition);
                } else {
                    Label emptySeatLabel = new Label();
                    emptySeatLabel.setPrefSize(170, 100);
                    gridlayout2.add(emptySeatLabel, xPosition, yPosition);
                }
                if (i % 3 == 0 && i % 6 != 0) { //set positions of the seat buttons
                    xPosition = 20;
                } else if (i % 6 == 0) {
                    xPosition = 6;
                    yPosition += 1;
                } else {
                    xPosition += 1;
                }

                for (String seatReferenceNumber : seatDetailsList.keySet()) {
                    if (seatReference.equals(seatReferenceNumber)) {
                        checkOutOfSeat.add(seatReference);
                    }
                }
            }

            Label descriptionLabel = new Label("Here`s a view of how currently available empty seats are arranged in the A/C compartment of 'Denuwara Menike' train");
            descriptionLabel.setFont(new Font("Arial Rounded MT Bold", 22));
            Label dateLabel = new Label("Departure Date:   " + dateSubstring);  //create labels,buttons and other elements
            Button backToMenu = new Button("Back to Menu");

            gridlayout2.add(descriptionLabel, 6, 1, 17, 1); //add created elements into the grid pane
            gridlayout2.add(dateLabel, 34, 6, 10, 1);
            gridlayout2.add(backToMenu, 34, 11, 17, 1);

            if (checkOutOfSeat.size() == SEATING_CAPACITY) {  //check whether all the seats have booked before
                Alert outOfSeatsAlert = new Alert(Alert.AlertType.WARNING);
                outOfSeatsAlert.setTitle("Out Of Seats Alert");
                outOfSeatsAlert.setContentText("Sorry, all the seats have been booked.There are no seats left.");
                outOfSeatsAlert.showAndWait();
            }

            backToMenu.setOnAction(event -> {
                Stage currentStage = (Stage) backToMenu.getScene().getWindow();
                currentStage.close();
            });

            Scene scene2 = new Scene(gridlayout2, 3000, 1000);
            Stage primaryStage2 = new Stage();
            primaryStage2.setTitle("Train Seat Booking System - View Empty Seats");
            primaryStage2.setScene(scene2);
            primaryStage2.showAndWait();
        }
    }

    public static void deleteCustomer(HashMap<String,String> customerDetailsList, HashMap<String,List<String>> seatDetailsList) { //let user to delete customer from a seat
        Scanner input = new Scanner(System.in);
        ArrayList<ArrayList<String>> deleteCustomerSeats = new ArrayList<>();
        System.out.println("Please enter the customer`s NIC number: ");
        String deleteCustomerNIC = input.nextLine().toLowerCase();
        while (!customerDetailsList.containsKey(deleteCustomerNIC)) { //check whether any seat reservations has been made under provided ID
            System.out.println("There`s no seats which have booked under NIC number " + deleteCustomerNIC + " Please re-enter the correct NIC number of the customer: ");
            deleteCustomerNIC = input.nextLine().toLowerCase();
        }
        for (String key : seatDetailsList.keySet()) {
            if (seatDetailsList.get(key).get(3).contains(deleteCustomerNIC)) {
                ArrayList<String> seatDetails = new ArrayList<>(seatDetailsList.get(key));
                deleteCustomerSeats.add(seatDetails);
            }
        }

        System.out.println("Here are the seats that customer has booked under NIC number " + deleteCustomerNIC);
        int deleteOptionNumber = 1;
        for (ArrayList<String> seatData : deleteCustomerSeats) { //show all the seats booked with provided ID
            String date = seatData.get(0).substring(0,10);
            String from = seatData.get(1);
            String to = seatData.get(2);
            String seat = seatData.get(4);

            System.out.println("Delete Option Number: " + deleteOptionNumber);
            System.out.println("     Departure Date: " + date );
            System.out.println("     From: " + from );
            System.out.println("     To: " + to );
            System.out.println("     Seat: " + seat );
            System.out.println();
            deleteOptionNumber+=1;
        }

        System.out.println("If you want to cancel all the bookings enter 'a' or if you want to cancel booking for a specific seat enter 'c' : ");
        String deleteOption = (input.nextLine()).toLowerCase();
        while (!deleteOption.equals("a") && !deleteOption.equals("c")) { //check whether the user has entered a valid input
            System.out.println("You have entered an invalid input.Please enter either 'a' or 'c' :");
            deleteOption = input.nextLine().toLowerCase();
        }
        if (deleteOption.equals("a")) {
            System.out.println("All the seat bookings which have made under NIC " + deleteCustomerNIC + " are canceled.");
            System.out.println("Here are the deleted records");
            for (ArrayList<String> seatDetails : deleteCustomerSeats) { //display all the seat details which are going to delete
                String date = seatDetails.get(0).substring(0,10);
                String from = seatDetails.get(1);
                String to = seatDetails.get(2);
                String seat = seatDetails.get(4);

                System.out.println("     Departure Date: " + date );
                System.out.println("     From: " + from );
                System.out.println("     To: " + to );
                System.out.println("     Seat: " + seat );
                System.out.println();
            }
            for (ArrayList<String> seatDetails : deleteCustomerSeats) { //remove all the bookings which has made using provided ID
                String seatReference = seatDetails.get(4) + seatDetails.get(0);
                seatDetailsList.remove(seatReference);
            }
            customerDetailsList.remove(deleteCustomerNIC);
        } else {
            boolean notInt = true;
            System.out.println("Enter the given delete option number of the seat that you want to cancel booking: ");
            while (notInt) {
                if (input.hasNextInt()) {
                    int deleteSeatNumber = input.nextInt();
                    if (deleteSeatNumber > deleteCustomerSeats.size()) {
                        boolean notInOptions = true;
                        System.out.println("You have entered an invalid delete option number.Please re-enter the correct option number: ");
                        while (notInOptions) {
                            if (input.hasNextInt()) { //check whether the input is an integer
                                deleteSeatNumber = input.nextInt();
                                if (deleteSeatNumber <= deleteCustomerSeats.size()) { //check whether the input is among provided delete option numbers
                                    notInOptions = false;
                                    notInt = false;

                                    ArrayList<String> selectedDeleteOptionDetails = deleteCustomerSeats.get(deleteSeatNumber - 1);
                                    String date = selectedDeleteOptionDetails.get(0).substring(0, 10);
                                    String from = selectedDeleteOptionDetails.get(1);
                                    String to = selectedDeleteOptionDetails.get(2);
                                    String seat = selectedDeleteOptionDetails.get(4);
                                    String seatReference = seat + selectedDeleteOptionDetails.get(0);

                                    seatDetailsList.remove(seatReference); //delete the selected seat booking
                                    if (deleteCustomerSeats.size() == 1) {
                                        customerDetailsList.remove(deleteCustomerNIC);
                                    }

                                    System.out.println("Seat booking that you have made for seat number " + seat + " is canceled.");
                                    System.out.println("Here`s the deleted record.");
                                    System.out.println();
                                    System.out.println("     Departure Date: " + date);
                                    System.out.println("     From: " + from);
                                    System.out.println("     To: " + to);
                                    System.out.println("     Seat: " + seat);
                                    System.out.println();
                                } else {
                                    System.out.println("You have entered an invalid delete option number.Please re-enter the correct option number: ");
                                    input.next();
                                }
                            } else {
                                System.out.println("You have entered an invalid delete option number.Please re-enter the correct option number: ");
                                input.next();
                            }
                        }
                    } else {
                        ArrayList<String> selectedDeleteOptionDetails = deleteCustomerSeats.get(deleteSeatNumber - 1);
                        String date = selectedDeleteOptionDetails.get(0).substring(0, 10);
                        String from = selectedDeleteOptionDetails.get(1);
                        String to = selectedDeleteOptionDetails.get(2);
                        String seat = selectedDeleteOptionDetails.get(4);
                        String seatReference = seat + selectedDeleteOptionDetails.get(0);

                        seatDetailsList.remove(seatReference); //delete the selected booking
                        if (deleteCustomerSeats.size() == 1) {
                            customerDetailsList.remove(deleteCustomerNIC);
                        }

                        System.out.println("Seat booking that you have made for seat number " + seat + " is canceled.");
                        System.out.println("Here`s the deleted record.");
                        System.out.println();
                        System.out.println("     Departure Date: " + date);
                        System.out.println("     From: " + from);
                        System.out.println("     To: " + to);
                        System.out.println("     Seat: " + seat);
                        System.out.println();

                        notInt = false;
                    }
                } else {
                    System.out.println("You have to enter a valid integer from given numbers as the delete option number.Please re-enter the correct option number: ");
                    input.next();
                }
            }
        }
    }

    public static void findSeatByCustomerName(HashMap<String,String> customerDetailsList, HashMap<String,List<String>> seatDetailsList) { //let user to find reserved seats by customer name
        Scanner input = new Scanner(System.in);
        ArrayList<ArrayList<String>> findCustomerSeats = new ArrayList<>();
        System.out.println("Please enter the customer`s name: ");
        String customerName = input.nextLine().toLowerCase();
        while (!customerDetailsList.containsValue(customerName)) {  //check whether the provided customer has booked any seats
            System.out.println("There`s no seats which have booked under customer name " + customerName + ".Please re-enter the customer name: ");
            customerName = input.nextLine().toLowerCase();
        }
        for (String id : customerDetailsList.keySet()) {
            if (customerDetailsList.get(id).equals(customerName)) {
                for (String seatReference : seatDetailsList.keySet()) {
                    if (seatDetailsList.get(seatReference).get(3).contains(id)) {
                        ArrayList<String> seatDetails = new ArrayList<>(seatDetailsList.get(seatReference));
                        findCustomerSeats.add(seatDetails);
                    }
                }
            }
        }
        System.out.println("Here are the seats that customer has booked: ");
        System.out.println();
        for (ArrayList<String> seatDetails : findCustomerSeats) { //print the seats booked by customer
            String date = seatDetails.get(0).substring(0,10);
            String from = seatDetails.get(1);
            String to = seatDetails.get(2);
            String id = seatDetails.get(3);
            String seat = seatDetails.get(4);
            System.out.println("     Customer NIC number: " + id );
            System.out.println("     Departure Date: " + date );
            System.out.println("     From: " + from );
            System.out.println("     To: " + to );
            System.out.println("     Seat: " + seat );
            System.out.println();
        }
    }

    public static void storeDataIntoFile(HashMap<String,String> customerDetailsList, HashMap<String,List<String>> seatDetailsList) { //let user to store all the booking details into the database
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase mongoDatabase = mongoClient.getDatabase("TrainSeatBookingSystem");
        MongoCollection<Document> collection = mongoDatabase.getCollection("CustomerDetails");
        BasicDBObject bookedSeatDetails = new BasicDBObject();
        collection.deleteMany(bookedSeatDetails);

        for (String seatReference : seatDetailsList.keySet()) { //add booked seat details to database
            String date = seatDetailsList.get(seatReference).get(0).substring(0,10);
            String path = seatDetailsList.get(seatReference).get(0).substring(10);
            String from = seatDetailsList.get(seatReference).get(1);
            String to = seatDetailsList.get(seatReference).get(2);
            String id = seatDetailsList.get(seatReference).get(3);
            String seat = seatDetailsList.get(seatReference).get(4);
            String customerName = customerDetailsList.get(id);
            Document document = new Document("Customer_Name", customerName)
                    .append("Customer_NIC", id)
                    .append("Departure_Date",date)
                    .append("Path",path)
                    .append("From",from)
                    .append("To",to)
                    .append("Seat_Number",seat);
            collection.insertOne(document);
        }
        System.out.println("All the booking details has been successfully stored into the file.");
    }

    public static void loadDataFromFile(HashMap<String,String> customerDetailsList, HashMap<String,List<String>> seatDetailsList) { //let user to load all the seat booking details from database
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase mongoDatabase = mongoClient.getDatabase("TrainSeatBookingSystem");
        MongoCollection<Document> collection = mongoDatabase.getCollection("CustomerDetails");
        FindIterable<Document> documentData = collection.find();
        for (Document details : documentData) { //get booking data from database to data structure
            customerDetailsList.put(details.getString("Customer_NIC"), details.getString("Customer_Name"));
            ArrayList<String> seatDataList = new ArrayList<>();
            String datePathReference = details.getString("Departure_Date") + details.getString("Path");
            seatDataList.add(datePathReference);
            seatDataList.add(details.getString("From"));
            seatDataList.add(details.getString("To"));
            seatDataList.add(details.getString("Customer_NIC"));
            seatDataList.add(details.getString("Seat_Number"));
            String seatReference = details.getString("Seat_Number") + datePathReference;
            seatDetailsList.put(seatReference, seatDataList);
        }
        System.out.println("All the booking details has been successfully loaded from the file.");
    }

    public static void viewSeatsOrderedAlphabeticallyByName(HashMap<String,String> customerDetailsList, HashMap<String,List<String>> seatDetailsList) { //allows user to view seats ordered alphabetically by name
        ArrayList<String> alphabeticallyOrderedNames = new ArrayList<>();
        System.out.println("Here`s a list of reserved seats which have been ordered alphabetically, by customer name.");
        System.out.println();
        for (String id : customerDetailsList.keySet()) {
            String customerName = customerDetailsList.get(id);
            if (!alphabeticallyOrderedNames.contains(customerName)) {  //add customer names in to an array list
                alphabeticallyOrderedNames.add(customerName);
            }
        }
        for(int i=0; i<alphabeticallyOrderedNames.size(); i++) { //sort names using bubble sorting algorithm
            for(int j=0; j<alphabeticallyOrderedNames.size()-i-1;j++ ) {
                if(alphabeticallyOrderedNames.get(j).compareTo(alphabeticallyOrderedNames.get(j+1)) > 0) {
                    String temporaryVariable = alphabeticallyOrderedNames.get(j);
                    alphabeticallyOrderedNames.set(j,alphabeticallyOrderedNames.get(j+1));
                    alphabeticallyOrderedNames.set(j+1,temporaryVariable);
                }
            }
        }
        for (String name : alphabeticallyOrderedNames) {  //print the seats ordered alphabetically by customer name
            for (String id : customerDetailsList.keySet()) {
                if (customerDetailsList.get(id).equals(name)) {
                    System.out.println("Customer name: " + name);
                    System.out.println("Customer NIC number: " + id);
                    System.out.println();
                    for (String seatReference : seatDetailsList.keySet()) {
                        if (seatDetailsList.get(seatReference).get(3).contains(id)) {
                            ArrayList<String> seatDetails = new ArrayList<>(seatDetailsList.get(seatReference));
                            String date = seatDetails.get(0).substring(0, 10);
                            String from = seatDetails.get(1);
                            String to = seatDetails.get(2);
                            String seat = seatDetails.get(4);
                            System.out.println("     Departure Date: " + date);
                            System.out.println("     From: " + from);
                            System.out.println("     To: " + to);
                            System.out.println("     Seat: " + seat);
                            System.out.println();
                        }
                    }
                }
            }
            System.out.println();
        }
    }

}
