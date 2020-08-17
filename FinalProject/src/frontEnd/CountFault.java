package frontEnd;

public class CountFault {
	int RMNoASA = 100;
	int RMNoEUA = 101;
	int RMNoNAA = 102;
	int RMNoASB = 200;
	int RMNoEUB = 201;
	int RMNoNAB = 202;
	int RMNoASC = 300;
	int RMNoEUC = 301;
	int RMNoNAC = 302;
	
	int countRMNoASA = 0;
	int countRMNoEUA = 0;
	int countRMNoNAA = 0;
	int countRMNoASB = 0;
	int countRMNoEUB = 0;
	int countRMNoNAB = 0;
	int countRMNoASC = 0;
	int countRMNoEUC = 0;
	int countRMNoNAC = 0;
	
	
	public CountFault(){
		
	}
	
	public void setFaultCount(int RMno) {
		if(RMno == RMNoASA) {
			countRMNoASA++;
		} else if (RMno == RMNoEUA) {
			countRMNoEUA++;
		} else if (RMno == RMNoNAA) {
			countRMNoNAA++;
		} else if(RMno == RMNoASB) {
			countRMNoASB++;
		} else if (RMno == RMNoEUB) {
			countRMNoEUB++;
		} else if (RMno == RMNoNAB) {
			countRMNoNAB++;
		} else if(RMno == RMNoASC) {
			countRMNoASC++;
		} else if (RMno == RMNoEUC) {
			countRMNoEUC++;
		} else if (RMno == RMNoNAC) {
			countRMNoNAC++;
		}
		
		
	}
	
	public int getFaultCount(int RMno) {
				
		if(RMno == RMNoASA) {
			return countRMNoASA;
		} else if (RMno == RMNoEUA) {
			return countRMNoEUA;
		} else if (RMno == RMNoNAA) {
			return countRMNoNAA;
		} else if(RMno == RMNoASB) {
			return countRMNoASB;
		} else if (RMno == RMNoEUB) {
			return countRMNoEUB;
		} else if (RMno == RMNoNAB) {
			return countRMNoNAB;
		} else if(RMno == RMNoASC) {
			return countRMNoASC;
		} else if (RMno == RMNoEUC) {
			return countRMNoEUC;
		} else if (RMno == RMNoNAC) {
			return countRMNoNAC;
		}
		return RMno;
	}

}