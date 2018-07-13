package com.udacity.popularmovies2.movietime.utils;


import com.udacity.popularmovies2.movietime.model.details.Genre;

import java.util.List;

public class MiscFunctions {


    public String genreFromText(List<Genre> genreList) {
        StringBuilder returnString = new StringBuilder();
        for (int i = 0; i < genreList.size(); i++) {
            returnString.append(genreList.get(i).getName()).append(", ");
        }

        return returnString.toString();
    }


    public String genreFromID(List<Integer> genreList) {
        StringBuilder returnString = new StringBuilder();
        for (int i = 0; i < genreList.size(); i++) {
            returnString.append(getgenreNameFromID(genreList.get(i))).append(", ");
        }

        return returnString.toString();
    }

    private String getgenreNameFromID(Integer id) {
        String retString;
        switch (id) {
            case 28:
                retString = "Action";
                break;
            case 16:
                retString = "Animation";
                break;
            case 12:
                retString = "Adventure";
                break;
            case 80:
                retString = "Crime";
                break;
            case 35:
                retString = "Comedy";
                break;
            case 18:
                retString = "Drama";
                break;
            case 99:
                retString = "Documentary";
                break;
            case 14:
                retString = "Fantasy";
                break;
            case 10751:
                retString = "Family";
                break;
            case 27:
                retString = "Horror";
                break;
            case 36:
                retString = "History";
                break;
            case 9648:
                retString = "Mystery";
                break;
            case 10402:
                retString = "Music";
                break;
            case 53:
                retString = "Thriller";
                break;
            case 10770:
                retString = "TV Movie";
                break;
            case 878:
                retString = "Science Fiction";
                break;
            case 10749:
                retString = "Romance";
                break;
            case 37:
                retString = "Western";
                break;
            case 10752:
                retString = "War";
                break;
            default:
                retString = "";
                break;

        }
        return retString;

    }
}
