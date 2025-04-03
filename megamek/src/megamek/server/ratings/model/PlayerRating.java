package megamek.server.ratings.model;

public class PlayerRating {
    private double rating;

    public PlayerRating(double initialRating) {
        this.rating = initialRating;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
