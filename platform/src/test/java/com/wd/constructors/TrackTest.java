package com.wd.constructors;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class TrackTest {

    @Test
    void traditionalBuilder() {
        Track track = new Track.Builder().withArtist("Dire Straits")
                .withTitle("Brothers In Arms")
                .withReleaseDate(LocalDate.of(1985, 6, 1))
                .build();

        assertThat(track.getArtist()).isEqualTo("Dire Straits");
    }

    @Test
    void functionalBuilder() {
        Track track = Builder.of(Track::new)
                .with(Track::setArtist, "Dire Straits")
                .with(Track::setTitle, "Brother In Arms")
                .with(Track::setReleaseDate, LocalDate.of(1985, 6, 1))
                .build();

        assertThat(track.getArtist()).isEqualTo("Dire Straits");

    }
}