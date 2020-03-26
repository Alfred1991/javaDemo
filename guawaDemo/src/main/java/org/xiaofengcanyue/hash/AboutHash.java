package org.xiaofengcanyue.hash;

import com.google.common.base.Charsets;
import com.google.common.hash.*;

import java.util.ArrayList;
import java.util.Collection;

public class AboutHash {

    public static void main(String[] args) {

    }

    /**
     * Provided Hash Functions:
     * md5()
     * murmur3_128()
     * murmur3_32()
     * sha1()
     * sha256()
     * sha512()
     * goodFastHash(int bits)
     */
    public static void useOfHashFunction(){
        HashFunction hf = Hashing.md5();
        HashCode hc = hf.newHasher()
                .putLong(1l)
                .putString("name",Charsets.UTF_8)
                .putObject(new Object(),useOfFunnel())
                .hash();
    }

    public static Funnel useOfFunnel(){
        Funnel<Person> personFunnel = new Funnel<Person>() {
            @Override
            public void funnel(Person person, PrimitiveSink into) {
                into
                        .putInt(person.id)
                        .putString(person.firstName, Charsets.UTF_8)
                        .putString(person.lastName, Charsets.UTF_8)
                        .putInt(1);
            }
        };
        return personFunnel;
    }

    /**
     * A Bloom filter is a data structure designed to tell you, rapidly and memory-efficiently, whether an element is present in a set.
     * The price paid for this efficiency is that a Bloom filter is a probabilistic data structure:
     *   it tells us that the element either definitely is not in the set or may be in the set.
     *
     */
    public static void useOfBloomFilter(){
        Collection<Person> friendsList = new ArrayList<>();
        Person dude = null;

        BloomFilter<Person> friends = BloomFilter.create(useOfFunnel(), 500, 0.01);
        for (Person friend : friendsList) {
            friends.put(friend);
        }
        // much later
        if (friends.mightContain(dude)) {
            // the probability that dude reached this place if he isn't a friend is 1%
            // we might, for example, start asynchronously loading things for dude while we do a more expensive exact check
        }
    }


    private static class Person {
        final int id;
        final String firstName;
        final String lastName;
        final int birthYear;

        public Person(int id,String firstName,String lastName,int birthYear){
            this.id=id;
            this.firstName=firstName;
            this.lastName=lastName;
            this.birthYear=birthYear;
        }

        public int getId() {
            return id;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public int getBirthYear() {
            return birthYear;
        }


    }

}
