package com.bjpowernode.vo;

/**
 *
 */
public class ProvinceCity {

    private String name;
    private Integer id;


    private Integer cid;
    private String cname;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCid() {
        return cid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    @Override
    public String toString() {
        return "ProvinceCity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", cid=" + cid +
                ", cname='" + cname + '\'' +
                '}';
    }
}
