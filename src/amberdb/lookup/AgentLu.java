package amberdb.lookup;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class AgentLu {

    @Column
    Long id;
    @Column
    String agent;
    @Column
    String workingDirectory;

    public AgentLu(long id, String agent, String workingDirectory) {
        this.id = id;
        this.agent = agent;
        this.workingDirectory = workingDirectory;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

}
