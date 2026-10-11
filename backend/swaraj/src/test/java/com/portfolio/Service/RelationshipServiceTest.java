package com.portfolio.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.portfolio.model.Users;

class RelationshipServiceTest {

    @Test
    void friendsCountShouldIncludeFamilyWhenFriendsListIsNull() {
        RelationshipService service = new CountingRelationshipService(null, null, List.of(createUser("fam-1"), createUser("fam-2")));

        assertEquals(2, service.friendsCount());
    }

    @Test
    void followersCountShouldCombineAllRelationshipLists() {
        RelationshipService service = new CountingRelationshipService(
                List.of(createUser("f1"), createUser("f2")),
                List.of(createUser("fr1")),
                List.of(createUser("fam-1"), createUser("fam-2"))
        );

        assertEquals(5, service.followersCount());
    }

    private static Users createUser(String username) {
        Users user = new Users();
        user.setUsername(username);
        return user;
    }

    private static class CountingRelationshipService extends RelationshipService {
        private final List<Users> followers;
        private final List<Users> friends;
        private final List<Users> family;

        private CountingRelationshipService(List<Users> followers, List<Users> friends, List<Users> family) {
            this.followers = followers;
            this.friends = friends;
            this.family = family;
        }

        @Override
        public List<Users> followers() {
            return followers;
        }

        @Override
        public List<Users> friends() {
            return friends;
        }

        @Override
        public List<Users> family() {
            return family;
        }
    }
}
