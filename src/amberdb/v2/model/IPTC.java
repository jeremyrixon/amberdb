package amberdb.v2.model;

import amberdb.v2.model.mapper.IPTCMapper;
import amberdb.v2.model.mapper.MapWith;

@MapWith(IPTCMapper.class)
public class IPTC extends Node {
    private String province;
    private String city;

    public IPTC(int id, int txn_start, int txn_end, String province, String city) {
        super(id, txn_start, txn_end);
        this.province = province;
        this.city = city;
    }

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
