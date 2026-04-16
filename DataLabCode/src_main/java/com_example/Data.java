package com.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Data {

    // Instance Variables
    private String gameTitle;
    private double userRating;
    private String releaseDate;
    private String genre;
    private double posPercent;
    private String highestTitle;

    // Default constructor
    public Data() {
        gameTitle = "N/A";
        userRating = 0;
        releaseDate = "";
        genre = "N/A";
    }

    // Initializing constructor
    public Data(String title, double rating, String date, String genre) {
        this.gameTitle = title;
        this.userRating = rating;
        this.releaseDate = date;
        this.genre = genre;
    }

    // Accessors
    public String getTitle() {
        return gameTitle;
    }

    public double getUserRating() {
        return userRating;
    }

    public String getDate() {
        return releaseDate;
    }

    public String getGenre() {
        return genre;
    }

    // Methods
    public String toString() {
        return ("Game: " + gameTitle + " | User Rating: " + userRating + " | Release date: " + releaseDate
                + " | Genre: " + genre);
    }

    public void run() {
        Data[] dataList = new Data[513250];

        String csvFile = "metacritic_pc_games.csv";
        String line = "";
        int index = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] values = line.split(";", -1); // -1 to keep trailing empty strings
                if (values.length == 4) {
                    String title = values[0];
                    String ratingStr = values[3].trim();
                    double rating;
                    if (ratingStr.isEmpty()) {
                        rating = 0.0;
                    } else {
                        try {
                            rating = Double.parseDouble(ratingStr);
                        } catch (NumberFormatException nfe) {
                            rating = 0.0;
                        }
                    }
                    String date = values[1];
                    String genre = values[2];

                    Data data = new Data(title, rating, date, genre);
                    dataList[index] = data;
                    index++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
         * Print all Ratings
         * for (Data data : dataList){
         * System.out.println(data.toString());
         * }
         */

        // Find total number of unique game titles
        String currentTitle = "";
        int numTitles = 0;
        for (Data data : dataList) {
            if (data != null) {
                if (!data.getTitle().equals(currentTitle)) {
                    numTitles++;
                    currentTitle = data.getTitle();
                }
            }
        }
        System.out.println("The total number of unique game titles is: " + numTitles);

        // Creating variables needed for average and standard deviation calculations
        double totalRatings = 0;
        double overallAvgRating = 0;
        double ratingDiff = 0;
        double sumRatingDiff = 0;
        double ratingStdDev = 0;

        // Arrays to hold sums of ratings, individual average ratings, titles, genres, dates, and number of reviews
        double[] ratingSums = new double[numTitles];
        double[] ratingAvgs = new double[numTitles];
        String[] titles = new String[numTitles];
        String[] genres = new String[numTitles];
        String[] dates = new String[numTitles];
        int[] numPos = new int[numTitles];
        int[] numReviews = new int[numTitles];

        // Find sums of user ratings by game titles (+ store number of reviews, titles, genres, and dates)
        titles[0] = dataList[0].getTitle();
        genres[0] = dataList[0].getGenre();
        dates[0] = dataList[0].getDate();
        currentTitle = dataList[0].getTitle();
        int titleIndex = 0;
        int numOfReviews = 0;
        int posReviews = 0;
        for (int i = 0; i < dataList.length; i++) {
            Data data = dataList[i];
            if (data == null)
                continue;
            if (data.getTitle().equals(currentTitle)) {
                ratingSums[titleIndex] += data.getUserRating();
                numOfReviews++;
                if (data.getUserRating() >= 6) {
                    posReviews++;
                }
            } else {
                numReviews[titleIndex] = numOfReviews;
                numPos[titleIndex] = posReviews;
                numOfReviews = 1;
                posReviews = 0;
                titleIndex++;
                if (titleIndex >= numTitles)
                    break;
                currentTitle = data.getTitle();
                titles[titleIndex] = currentTitle;
                genres[titleIndex] = data.getGenre();
                dates[titleIndex] = data.getDate();
                ratingSums[titleIndex] += data.getUserRating();
            }
        }

        // Calculate average ratings for each title
        for (int i = 0; i < numTitles; i++) {
            if (numReviews[i] > 0) {
                ratingAvgs[i] = ratingSums[i] / numReviews[i];
            } else {
                ratingAvgs[i] = 0;
            }
        }

        // Calculate average user rating by game titles
        for (double avg : ratingAvgs) {
            totalRatings += avg;
        }

        overallAvgRating = totalRatings / numTitles;
        System.out.println("The average user rating by game titles is: " + overallAvgRating);

        // Calculate standard deviation of user ratings by game titles
        for (double avg : ratingAvgs) {
            ratingDiff = avg - overallAvgRating;
            sumRatingDiff += ratingDiff * ratingDiff;
        }
        ratingStdDev = Math.sqrt(sumRatingDiff / numTitles);
        System.out.println("The standard deviation of user ratings by game titles is: " + ratingStdDev);

        // Find which game title has the highest user rating average with its release date & genre
        double highestRating = ratingAvgs[0];
        highestTitle = titles[0];
        String highestDate = dates[0];
        String highestGenre = genres[0];
        int highestPositiveReviews = numPos[0];
        int highestNumReviews = numReviews[0];

        Scanner scanner = new Scanner(System.in); // Get user input for minimum number of reviews to consider
        System.out.print("Enter the minimum number of reviews a game must have to be considered (choose between 1 and 4070): ");
        int userInput = scanner.nextInt();

        // Validate user input
        while (userInput < 1 || userInput > 4070) { // 4070 is the max number of reviews for a single game in the dataset
            System.out.println("Invalid input. Please enter a number between 1 and 4070.");
            System.out.print("Enter the minimum number of reviews a game must have to be considered: ");
            userInput = scanner.nextInt();
            if (userInput >= 1 && userInput <= 4070) {
                break;
            }
        }

        // Find highest rated game title that meets the user input criteria
        for (Data data : dataList) {
            if (data != null) {
                for (int i = 0; i < ratingAvgs.length; i++) {
                    if (ratingAvgs[i] > highestRating && numReviews[i] >= userInput) {
                        highestRating = ratingAvgs[i];
                        highestTitle = titles[i];
                        highestDate = dates[i];
                        highestGenre = genres[i];
                        highestPositiveReviews = numPos[i];
                        highestNumReviews = numReviews[i];
                    }
                }
            }
        }
        // Test 
        //System.out.println(highestPositiveReviews);
        //System.out.println(highestNumReviews);

        // Calculate how many standard deviations above the average the highest rating is
        double numStdDevs = (highestRating - overallAvgRating) / ratingStdDev;
        // Calculate percentage of postive reviews (>=6) in highest rated title
        posPercent = Math.floor( ((double) highestPositiveReviews / highestNumReviews) * 100);
        System.out.println("The game title with the highest user rating average is: [" + highestTitle + "] with an average\nof ["+ highestRating + "], released in ["
                + highestDate + "] in the [" + highestGenre + "].\nIt is [" + numStdDevs+ "] standard deviations above the overall average in the dataset.");
        System.out.println("The games positive review percent: " + posPercent);
        System.out.println("Total number of reviews: " + highestNumReviews);
        scanner.close();
    }
    public double getPosPercent(){
        return posPercent;
    }
    public String getHighestTitle(){
        return highestTitle;
    }
}