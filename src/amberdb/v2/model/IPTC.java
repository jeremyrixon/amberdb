package amberdb.v2.model;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class IPTC extends Node {

    @Column
    private String province;
    @Column
    private String city;

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
