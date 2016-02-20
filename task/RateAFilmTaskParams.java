package uk.co.odeon.androidapp.task;

public class RateAFilmTaskParams {
    private String email;
    private int filmMasterId;
    private String password;
    private int rating;

    public RateAFilmTaskParams(String email, String password, int halfRating, int filmMasterId) {
        this.email = null;
        this.password = null;
        this.rating = 0;
        this.filmMasterId = 0;
        this.email = email;
        this.password = password;
        this.rating = halfRating;
        this.filmMasterId = filmMasterId;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRating() {
        return this.rating;
    }

    public void setRating(int halfRating) {
        this.rating = halfRating;
    }

    public int getFilmMasterId() {
        return this.filmMasterId;
    }

    public void setFilmMasterId(int filmMasterId) {
        this.filmMasterId = filmMasterId;
    }
}
