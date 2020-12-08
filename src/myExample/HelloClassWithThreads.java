package myExample;

public class HelloClassWithThreads {
    public static void main(String[] args){

    }
}

class Monitor{
    public void display(){
        
    }
}

class Person implements Runnable{
    private String name;
    private String[] messages;
    Monitor screen;

    public Person(String name, String[] messages, Monitor screen) {
        this.name = name;
        this.messages = messages;
        this.screen=screen;
    }

    @Override
    public void run() {
        screen.display();
    }
}
