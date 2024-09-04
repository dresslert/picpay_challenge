package br.com.dressler.apppicpay.authorization;

public record Authorization (String message){
    public boolean isAuthorized() {
        return message.equals("success");
    }
}
