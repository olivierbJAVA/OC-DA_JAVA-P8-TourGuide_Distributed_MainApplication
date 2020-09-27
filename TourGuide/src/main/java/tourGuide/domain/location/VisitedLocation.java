package tourGuide.domain.location;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.UUIDDeserializer;
import com.fasterxml.jackson.databind.ser.std.UUIDSerializer;

import java.util.Date;
import java.util.UUID;

public class VisitedLocation {

    //@JsonSerialize(using= UUIDSerializer.class)
    //@JsonDeserialize(using= UUIDDeserializer.class)
    public UUID userId;
    public Location location;
    public Date timeVisited;

    public VisitedLocation(UUID userId, Location location, Date timeVisited) {
        this.userId = userId;
        this.location = location;
        this.timeVisited = timeVisited;
    }

    public VisitedLocation() {
    }
}
