package ro.tuc.ds2020;

public class Message {
    private String sender;
    private String receiver;
    private String content;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Message(String sender, String receiver, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
    }

    //o reprezentare sub forma text a unui obiect message
    @Override
    public String toString() {
        return "Message{" +
                "sender='" + sender + '\'' +   //'\' pt  apostrof: 'diia'
                ", receiver='" + receiver + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
