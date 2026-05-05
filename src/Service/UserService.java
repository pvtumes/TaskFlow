package Service;

import Model.Enums.AuthStatus;
import Model.User;
import Repository.UserRepository;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public AuthStatus registerUser(String name,String email,String password){
        if(name == null || name.isBlank() ||
                email == null || email.isBlank() ||
                password == null || password.isBlank()){
            return AuthStatus.INVALID_INPUT;
        }

        if(userRepository.findByEmail(email).isPresent()){
            return AuthStatus.EMAIL_EXIST;
        }

        User newUser=new User(name,email,password);
        userRepository.save(newUser);
        return AuthStatus.SUCCESS;

    }
    public AuthStatus loginUser(String email,String password){
        if(email == null || email.isBlank() || password == null){
            return AuthStatus.INVALID_INPUT;
        }
        var optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isPresent()){
            User user=optionalUser.get();
            if(user.getPassword().equals(password)){
                return AuthStatus.SUCCESS;
            }
            return AuthStatus.INVALID_PASSWORD;

        }
        return AuthStatus.USER_NOT_FOUND;
    }
}
