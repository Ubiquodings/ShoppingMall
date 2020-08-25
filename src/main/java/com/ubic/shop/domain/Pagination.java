package com.ubic.shop.domain;

public class Pagination {

    private int listSize = 10;                //초기값으로 목록개수를 10으로 셋팅
    private int rangeSize = 10;            //초기값으로 페이지범위를 10으로 셋팅
    private int page=1;//초기값으로 시작 페이제를 1로 세팅
    private int range=1;
    private int listCnt;
    private double pageCnt;
    private int startPage = 1;
    private int startList;
    private int endPage;
    private boolean prev=false;
    private boolean next=true;

    public int getRangeSize() {
        return rangeSize;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;

    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public int getStartPage() {
        return startPage;
    }

    public void setStartPage(int startPage) {
        this.startPage = startPage;
    }

    public int getEndPage() {
        return endPage;
    }

    public void setEndPage(int endPage) {
        this.endPage = endPage;
    }

    public boolean isPrev() {
        return prev;
    }

    public void setPrev(boolean prev) {
        this.prev = prev;
    }

    public boolean isNext() {
        return next;
    }

    public void setNext(boolean next) {
        this.next = next;
    }

    public int getListSize() {
        return listSize;
    }

    public void setListSize(int listSize) {
        this.listSize = listSize;
    }

    public int getListCnt() {
        return listCnt;
    }

    public void setListCnt(int listCnt) {
        this.listCnt = listCnt;
    }

    public int getStartList() {
        return startList;
    }

    public void pageInfo(int page, int range, int listCnt) {
        this.page = page;
        this.range = range;
        this.listCnt = listCnt;

        //전체 페이지수
        this.pageCnt = Math.ceil((double) listCnt / listSize);

        //시작 페이지
        this.startPage = (range - 1) * rangeSize + 1;

        //끝 페이지
        this.endPage = range * rangeSize;

        //게시판 시작번호
        this.startList = (page - 1) * listSize;

        //이전 버튼 상태
        this.prev = range == 1 ? false : true;

        //다음 버튼 상태
        this.next = pageCnt > endPage ? true : false;

        if (this.endPage > this.pageCnt) {
            this.endPage = (int)this.pageCnt;
            this.next = false;
        }
    }
}
