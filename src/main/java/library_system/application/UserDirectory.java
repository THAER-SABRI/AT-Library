package library_system.application;

import java.util.List;

public interface UserDirectory {
    List<String> getAllUsers();
    void addUser(String id, String name, String phone, String email);
}
