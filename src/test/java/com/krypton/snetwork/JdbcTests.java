package com.krypton.snetwork;

import com.krypton.snetwork.model.group.Group;
import com.krypton.snetwork.model.user.User;
import com.krypton.snetwork.repository.GroupRepository;
import com.krypton.snetwork.repository.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

public class JdbcTests {

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    public void insertData() {

        Set<Group> projects = new HashSet<>();

        projects.add(new Group("Test",new User("m","ma@gmail.com","1")));

        User user = new User();

        assertEquals(0, user.getGroups().size());
        user.setGroups(projects);

        assertNotNull(user);
    }

    @Test
    public void readData() {

    }

}