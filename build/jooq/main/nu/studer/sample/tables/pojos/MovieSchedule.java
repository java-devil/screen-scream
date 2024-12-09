/*
 * This file is generated by jOOQ.
 */
package nu.studer.sample.tables.pojos;


import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class MovieSchedule implements Serializable {

    private static final long serialVersionUID = 1L;

    private final UUID booking;
    private final String imdbId;
    private final BigDecimal price;
    private final LocalDateTime showTime;

    public MovieSchedule(MovieSchedule value) {
        this.booking = value.booking;
        this.imdbId = value.imdbId;
        this.price = value.price;
        this.showTime = value.showTime;
    }

    public MovieSchedule(
        UUID booking,
        String imdbId,
        BigDecimal price,
        LocalDateTime showTime
    ) {
        this.booking = booking;
        this.imdbId = imdbId;
        this.price = price;
        this.showTime = showTime;
    }

    /**
     * Getter for <code>public.movie_schedule.booking</code>.
     */
    public UUID getBooking() {
        return this.booking;
    }

    /**
     * Getter for <code>public.movie_schedule.imdb_id</code>.
     */
    public String getImdbId() {
        return this.imdbId;
    }

    /**
     * Getter for <code>public.movie_schedule.price</code>.
     */
    public BigDecimal getPrice() {
        return this.price;
    }

    /**
     * Getter for <code>public.movie_schedule.show_time</code>.
     */
    public LocalDateTime getShowTime() {
        return this.showTime;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final MovieSchedule other = (MovieSchedule) obj;
        if (this.booking == null) {
            if (other.booking != null)
                return false;
        }
        else if (!this.booking.equals(other.booking))
            return false;
        if (this.imdbId == null) {
            if (other.imdbId != null)
                return false;
        }
        else if (!this.imdbId.equals(other.imdbId))
            return false;
        if (this.price == null) {
            if (other.price != null)
                return false;
        }
        else if (!this.price.equals(other.price))
            return false;
        if (this.showTime == null) {
            if (other.showTime != null)
                return false;
        }
        else if (!this.showTime.equals(other.showTime))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.booking == null) ? 0 : this.booking.hashCode());
        result = prime * result + ((this.imdbId == null) ? 0 : this.imdbId.hashCode());
        result = prime * result + ((this.price == null) ? 0 : this.price.hashCode());
        result = prime * result + ((this.showTime == null) ? 0 : this.showTime.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("MovieSchedule (");

        sb.append(booking);
        sb.append(", ").append(imdbId);
        sb.append(", ").append(price);
        sb.append(", ").append(showTime);

        sb.append(")");
        return sb.toString();
    }
}