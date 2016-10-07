package eu.h2020.symbiote.model;

/**
 * Created by Mael on 07/10/2016.
 */
public class ResourceUrlQueryModel {

    private String[] idList;

    public ResourceUrlQueryModel(String[] idList) {
        this.idList = idList;
    }

    public String[] getIdList() {
        return idList;
    }

    public void setIdList(String[] idList) {
        this.idList = idList;
    }
}
