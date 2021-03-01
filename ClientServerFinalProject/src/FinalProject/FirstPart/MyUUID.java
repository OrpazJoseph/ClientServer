package FinalProject.FirstPart;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class MyUUID implements Comparable<MyUUID> {

    private final UUID uuid;
    private final String key;

    public MyUUID(String key){
        this.key = key;
        this.uuid = MyUUID.Encoder(key);
    }

    public static UUID Encoder(@NotNull final String key){
        byte[] byteKey = key.getBytes();
        return UUID.nameUUIDFromBytes(byteKey);
    }

    public static String Decoder(@NotNull final MyUUID myUUID){
        return myUUID.getStringUUID();
    }

    public String getStringUUID(){
        return key;
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public int compareTo(@NotNull MyUUID o) {
        return this.getUuid().compareTo(o.getUuid());
    }

    @Override
    public String toString(){
        return "The key type is: " + key;
    }

}
