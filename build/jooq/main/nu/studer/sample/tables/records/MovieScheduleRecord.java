/*
 * This file is generated by jOOQ.
 */
package nu.studer.sample.tables.records;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import nu.studer.sample.tables.MovieSchedule;

import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class MovieScheduleRecord extends UpdatableRecordImpl<MovieScheduleRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.movie_schedule.booking</code>.
     */
    public MovieScheduleRecord setBooking(UUID value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>public.movie_schedule.booking</code>.
     */
    public UUID getBooking() {
        return (UUID) get(0);
    }

    /**
     * Setter for <code>public.movie_schedule.imdb_id</code>.
     */
    public MovieScheduleRecord setImdbId(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>public.movie_schedule.imdb_id</code>.
     */
    public String getImdbId() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.movie_schedule.price</code>.
     */
    public MovieScheduleRecord setPrice(BigDecimal value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>public.movie_schedule.price</code>.
     */
    public BigDecimal getPrice() {
        return (BigDecimal) get(2);
    }

    /**
     * Setter for <code>public.movie_schedule.show_time</code>.
     */
    public MovieScheduleRecord setShowTime(LocalDateTime value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>public.movie_schedule.show_time</code>.
     */
    public LocalDateTime getShowTime() {
        return (LocalDateTime) get(3);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<UUID> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached MovieScheduleRecord
     */
    public MovieScheduleRecord() {
        super(MovieSchedule.MOVIE_SCHEDULE);
    }

    /**
     * Create a detached, initialised MovieScheduleRecord
     */
    public MovieScheduleRecord(UUID booking, String imdbId, BigDecimal price, LocalDateTime showTime) {
        super(MovieSchedule.MOVIE_SCHEDULE);

        setBooking(booking);
        setImdbId(imdbId);
        setPrice(price);
        setShowTime(showTime);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised MovieScheduleRecord
     */
    public MovieScheduleRecord(nu.studer.sample.tables.pojos.MovieSchedule value) {
        super(MovieSchedule.MOVIE_SCHEDULE);

        if (value != null) {
            setBooking(value.getBooking());
            setImdbId(value.getImdbId());
            setPrice(value.getPrice());
            setShowTime(value.getShowTime());
            resetChangedOnNotNull();
        }
    }
}
