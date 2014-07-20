package kr.co.teamcloud.koreatour.common;

public class PaginationVo {

	private int currentPageNo = 1;
	private int recordCountPerPage = 10;
	private int pageSize = 5;
	private int totalRecordCount = 0;
	private int totalPageCount = 0;
	private int firstPageNoOnPageList;
	private int lastPageNoOnPageList;
	private int startRowNum;
	private int endRowNum;

	public int getTotalPageCount() {
		this.totalPageCount = ((this.getTotalRecordCount() - 1) / this.getRecordCountPerPage() + 1);
		return this.totalPageCount;
	}

	public int getFirstPageNo() {
		return 1;
	}

	public int getLastPageNo() {
		return getTotalPageCount();
	}

	public int getFirstPageNoOnPageList() {
		this.firstPageNoOnPageList = ((this.getCurrentPageNo() - 1) / this.getPageSize() * this.getPageSize() + 1);
		return this.firstPageNoOnPageList;
	}

	public int getLastPageNoOnPageList() {
		this.lastPageNoOnPageList = (this.getFirstPageNoOnPageList()
				+ this.getPageSize() - 1);
		if (this.lastPageNoOnPageList > this.getTotalPageCount()) {
			this.lastPageNoOnPageList = this.getTotalPageCount();
		}
		return this.lastPageNoOnPageList;
	}

	public int getStartRowNum() {
		this.startRowNum = ((this.getCurrentPageNo() - 1)
				* this.getRecordCountPerPage() + 1);
		return this.startRowNum;
	}

	public int getEndRowNum() {
		this.endRowNum = this.getStartRowNum() + this.getRecordCountPerPage()
				- 1;
		return this.endRowNum;
	}

	public int getTotalRecordCount() {
		return totalRecordCount;
	}

	public void setTotalRecordCount(int totalRecordCount) {
		this.totalRecordCount = totalRecordCount;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getRecordCountPerPage() {
		return recordCountPerPage;
	}

	public void setRecordCountPerPage(int recordCountPerPage) {
		this.recordCountPerPage = recordCountPerPage;
	}

	public int getCurrentPageNo() {
		return currentPageNo;
	}

	public void setCurrentPageNo(int currentPageNo) {
		this.currentPageNo = currentPageNo;
	}

}
