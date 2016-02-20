package uk.co.odeon.androidapp.model;

public class FilmListMyFilm extends FilmListFilm {
    public FilmCategory category;

    public enum FilmCategory {
        recommended,
        bookedAndNotRated,
        bookedAndRated,
        otherRatedFilms
    }

    public FilmListMyFilm(FilmCategory category) {
        this.category = category;
    }

    public FilmListMyFilm(FilmCategory category, Integer filmId, String filmText, String trailerUrl, String imageURL, String cert, boolean rateable, float halfrating, String genreText, String relDateText, boolean bbf) {
        super(filmId, filmText, trailerUrl, imageURL, cert, rateable, halfrating, genreText, relDateText, bbf);
        this.category = category;
    }
}
