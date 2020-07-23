package Controller;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;

public class AuthTokenController {
    private HashMap<String,TokenData> allTokens; // token to user!!!
    private static  AuthTokenController authTokenController;
    private final SecureRandom secureRandom=new SecureRandom();
    private AuthTokenController(){
        allTokens=new HashMap<>();
    }
    public static AuthTokenController getInstance(){
        if(authTokenController==null)
            authTokenController=new AuthTokenController();
        return authTokenController;
    }

    private String generateTokenString(){
        String tokenData="";
        byte[] random=new byte[24];
        while (true){
            secureRandom.nextBytes(random);
            tokenData=Base64.getEncoder().encodeToString(random);
            if(allTokens.containsKey(tokenData)==false){
                return tokenData;
            }
        }
    }

    public String getTokenForClient(String username){
        /////is there username and password?
        for(String key:allTokens.keySet()){
            if(allTokens.get(key).username.equals(username)){
                if(allTokens.get(key).expirationTime > System.currentTimeMillis()) return key;
                else{
                    String token=generateTokenString();
                    allTokens.remove(key);
                    allTokens.put(token,new TokenData(username));
                    return token;
                }
            }
        }
        String token=generateTokenString();
        allTokens.put(token,new TokenData(username));
        return token;
    }

    public String getUsernameByToken(String token){
        if(allTokens.containsKey(token)){
            TokenData value=allTokens.get(token);
            if(value.expirationTime > System.currentTimeMillis()) return value.username;
            else{
                return "token expired";
            }
        }
        else {
            return "token is invalid";
        }
    }

    public HashMap<String,TokenData> getAllTokens(){return allTokens;}

    class TokenData{
        long expirationTime;
        String username;
        public TokenData(String username){
            this.username=username;
            expirationTime=System.currentTimeMillis()+60*60*1000;////////15 min
        }
    }

}
