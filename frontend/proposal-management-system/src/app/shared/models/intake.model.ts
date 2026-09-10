export interface Account {
  id?: number;
  name: string;
  industry: string;
  primaryContact: string;
}

export interface ProposalRequest {
  id?: number;
  requirementsSummary: string;
  deadline: string; // ISO format: YYYY-MM-DD
  status?: string;
  accountId: number;
  accountName?: string;
  assignedAuthorId: number;
  assignedAuthorName?: string;
  departmentId: number;
  departmentName?: string;
  offeringId: number;
  offeringName?: string;
}

export interface DepartmentOption {
  id: number;
  name: string;
  active: boolean;
}

export interface OfferingOption {
  id: number;
  name: string;
  active: boolean;
}