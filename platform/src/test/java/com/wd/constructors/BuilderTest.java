package com.wd.constructors;

import com.wd.functional.NamedPredicate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BuilderTest {

    @Test
    void build() {

        Person person = Builder.of(Person::new)
                .with(Person::setName, "Otto")
                .with(Person::setAge, 5).build();

        assertThat(person.getAge()).isEqualTo(5);
        assertThat(person.getName()).isEqualTo("Otto");
    }

    @Test
    void buildWithVerifier() {

        NamedPredicate<Person> ageVerifier = new NamedPredicate<>("isOlderThan3", p -> p.getAge() > 3);
        assertThrows(IllegalStateException.class, () -> {
            Builder.of(Person::new)
                    .withVerifiers(ageVerifier)
                    .with(Person::setName, "Otto").with(Person::setAge, 2).build();
        });
    }
}