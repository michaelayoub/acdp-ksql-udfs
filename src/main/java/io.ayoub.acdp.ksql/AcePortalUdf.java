package io.ayoub.acdp.ksql;
import io.ayoub.acdp.portalDatabase.PortalDatabase;
import io.ayoub.acdp.portalDatabase.entities.SkillBase;
import io.ayoub.acdp.portalDatabase.tables.SkillTable;
import io.ayoub.acdp.portalDatabase.tables.XPTable;
import io.confluent.ksql.function.udf.UdfDescription;
import org.apache.kafka.common.Configurable;

import java.util.Map;

@UdfDescription(
        name = "ace_portal",
        author = "Michael Ayoub",
        version = "0.1",
        description = "A function for accessing data from client_portal.dat."
)
public class AcePortalUdf implements Configurable {
    SkillTable skillTable;
    XPTable xpTable;

    @Override
    public void configure(Map<String, ?> configs) {
        final var portalDatabase = new PortalDatabase();
        this.skillTable = portalDatabase.getSkillTable();
        this.xpTable = portalDatabase.getXPTable();
    }
}
