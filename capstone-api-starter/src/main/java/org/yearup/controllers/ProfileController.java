package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProfileDao;
import org.yearup.data.UserDao;
import org.yearup.models.Profile;
import org.yearup.models.User;

import java.security.Principal;

@RestController
@RequestMapping("/profile")
@CrossOrigin
public class ProfileController {
    private final ProfileDao profileDao;
    private final UserDao userDao;

    @Autowired
    public ProfileController(ProfileDao profileDao, UserDao userDao) {
        this.profileDao = profileDao;
        this.userDao = userDao;
    }

    // GET http://localhost:8080/profile
    @GetMapping
    //@PreAuthorize("isAuthenticated()")
    public ResponseEntity<Profile> getProfile(Principal principal) {
        try {

            String username = principal.getName();
             User user = userDao.getByUserName(username);
             int userId = user.getId();
            Profile profile = profileDao.getByUserId(userId);

             if (profile == null) {
                 return new ResponseEntity<>(HttpStatus.NOT_FOUND);
             }
            return new ResponseEntity<>(profile, HttpStatus.OK);


        }catch(Exception e){
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to retrieve profile"
            );
        }
    }

    // PUT http://localhost:8080/profile
    //@PreAuthorize("isAuthenticated()")
    @PutMapping
    public ResponseEntity<Void> updateProfile(
            @RequestBody Profile profile,
            Principal principal){
        try {
            String  username = principal.getName();
            User user = userDao.getByUserName(username);
            int userId = user.getId();
            profileDao.update(userId, profile);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to update profile"
            );
        }
    }

}
