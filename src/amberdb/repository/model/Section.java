package amberdb.repository.model;

import amberdb.relation.ExistsOn;

import javax.persistence.Column;

public class Section extends Work {
    @Column
    private String metsId;
    @Column
    private String captions;
    @Column
    private Boolean advertising;
    @Column
    private Boolean illustrated;
    @Column
    private String printedPageNumber;

    public Section() {
        super();
    }

    public String getMetsId() {
        return metsId;
    }

    public void setMetsId(String metsId) {
        this.metsId = metsId;
    }

    public String getCaptions() {
        return captions;
    }

    public void setCaptions(String captions) {
        this.captions = captions;
    }

    public Boolean getAdvertising() {
        return advertising;
    }

    public void setAdvertising(Boolean advertising) {
        this.advertising = advertising;
    }

    public Boolean getIllustrated() {
        return illustrated;
    }

    public void setIllustrated(Boolean illustrated) {
        this.illustrated = illustrated;
    }

    public String getPrintedPageNumber() {
        return printedPageNumber;
    }

    public void setPrintedPageNumber(String printedPageNumber) {
        this.printedPageNumber = printedPageNumber;
    }

    public Iterable<Page> getExistsOnPages() {
        return pageAssociationDao.existsOnPages(this.getId());
    }

    public Page getPage(int idx) {
        return pageAssociationDao.getPage(this.getId(), idx);
    }

    public Iterable<Work> getLeafs(String subType) {
        return pageAssociationDao.getLeafs(this.getId(), ExistsOn.label, subType);
    }

    public long getExistsOnRef(int position) {
        return pageAssociationDao.getPage(this.getId(), position).getId();
    }

    public int countExistsOns() {
        return edgeDao.countExistsOn(this.getId());
    }
}
