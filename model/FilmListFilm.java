package uk.co.odeon.androidapp.model;

public class FilmListFilm {
    public boolean bbf;
    public String cert;
    public Integer filmId;
    public String filmText;
    public String genreText;
    public float halfrating;
    public String imageURL;
    public boolean rateable;
    public String relDateText;
    public String trailerUrl;

    public FilmListFilm(Integer filmId, String filmText, String trailerUrl, String imageURL, String cert, boolean rateable, float halfrating, String genreText, String relDateText, boolean bbf) {
        this.filmId = filmId;
        this.filmText = filmText;
        this.trailerUrl = trailerUrl;
        this.imageURL = imageURL;
        this.cert = cert;
        this.rateable = rateable;
        this.halfrating = halfrating;
        this.genreText = genreText;
        this.relDateText = relDateText;
        this.bbf = bbf;
    }
}
