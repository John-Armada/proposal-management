package com.pointwest.prop.auth.model;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import lombok.NoArgsConstructor;

/**
 * *Static Role -> Permission mapping for the Proposal Management System.
 *
 * Derivation is documented in README.md alongside this class. In short:
 * each set below is built from the "Primary Permissions" column in SRS
 * Section 2.2, the per-module Business Rules in SRS Section 3, and the
 * per-endpoint Role column in the SDD Addendum Section 3.
 *
 * This mapping is necessary but not sufficient for authorization: some
 * SRS rules are identity-based (ownership, segregation-of-duty) rather
 * than role-based, and must be enforced as additional runtime checks in
 * the service layer on top of a successful permission check. See
 * README.md "Beyond RBAC" for the full list.
 */
@NoArgsConstructor
public final class RolePermissions {

    private static final Map<Role, Set<String>> MAP = new EnumMap<>(Role.class);

    static {
        MAP.put(Role.AUTHOR, Set.of(
                // Shared reference-data lookups
                Permission.DEPARTMENT_VIEW,
                Permission.OFFERING_VIEW,

                // *Module 1: Account & Intake — full CRUD on Accounts owned (SRS 2.2)
                Permission.ACCOUNT_VIEW,
                Permission.ACCOUNT_CREATE,
                Permission.ACCOUNT_EDIT,
                Permission.ACCOUNT_DELETE,
                Permission.PROPOSAL_REQUEST_VIEW,
                Permission.PROPOSAL_REQUEST_CREATE,
                Permission.PROPOSAL_REQUEST_EDIT, // + ownership check
                Permission.PROPOSAL_REQUEST_DELETE, // + ownership check
                Permission.REPORT_ACCOUNT_REQUEST_EXPORT,

                // *Module 2: Proposal Authoring & Templates
                Permission.PROPOSAL_VIEW,
                Permission.PROPOSAL_CREATE,
                Permission.PROPOSAL_EDIT, // + ownership check
                Permission.PROPOSAL_SUBMIT,
                Permission.TEMPLATE_VIEW,
                Permission.TEMPLATE_CREATE,
                Permission.TEMPLATE_EDIT,
                Permission.TEMPLATE_DELETE,
                Permission.REPORT_PROPOSAL_EXPORT,

                // *Module 3: Pricing & Line Items — owning Author may manage
                Permission.LINE_ITEM_VIEW,
                Permission.LINE_ITEM_CREATE, // + ownership check
                Permission.LINE_ITEM_EDIT, // + ownership check
                Permission.LINE_ITEM_DELETE, // + ownership check
                Permission.CATALOG_ITEM_VIEW,
                Permission.CATALOG_ITEM_CREATE,
                Permission.CATALOG_ITEM_EDIT,
                Permission.CATALOG_ITEM_DELETE,
                Permission.PRICING_SUMMARY_VIEW,
                Permission.REPORT_PRICING_EXPORT,

                // *Module 4: Review & Approval — Author can view own audit trail,
                // but cannot approve/reject (no REVIEW_DECIDE)
                Permission.REVIEW_VIEW,
                Permission.REPORT_AUDIT_TRAIL_EXPORT));

        MAP.put(Role.REVIEWER, Set.of(
                // Shared reference-data lookups
                Permission.DEPARTMENT_VIEW,
                Permission.OFFERING_VIEW,

                // *Module 1: Account & Intake — SDD lists Author+Reviewer on all
                // Account endpoints; Reviewer may edit/delete/reassign Requests
                // but does not create them (SDD 3.1 POST is Author-only)
                Permission.ACCOUNT_VIEW,
                Permission.ACCOUNT_CREATE,
                Permission.ACCOUNT_EDIT,
                Permission.ACCOUNT_DELETE,
                Permission.PROPOSAL_REQUEST_VIEW,
                Permission.PROPOSAL_REQUEST_EDIT,
                Permission.PROPOSAL_REQUEST_DELETE,
                Permission.PROPOSAL_REQUEST_REASSIGN,
                Permission.REPORT_ACCOUNT_REQUEST_EXPORT,

                // *Module 2: Proposal Authoring & Templates — Reviewer may edit
                // (SRS 3.2 rule) but does not create Proposals or manage
                // Templates (SDD 3.2 template endpoints are Author-only)
                Permission.PROPOSAL_VIEW,
                Permission.PROPOSAL_EDIT,
                Permission.REPORT_PROPOSAL_EXPORT,

                // *Module 3: Pricing & Line Items — SDD lists Author+Reviewer
                // on every line-item and catalog-item endpoint
                Permission.LINE_ITEM_VIEW,
                Permission.LINE_ITEM_CREATE,
                Permission.LINE_ITEM_EDIT,
                Permission.LINE_ITEM_DELETE,
                Permission.CATALOG_ITEM_VIEW,
                Permission.CATALOG_ITEM_CREATE,
                Permission.CATALOG_ITEM_EDIT,
                Permission.CATALOG_ITEM_DELETE,
                Permission.PRICING_SUMMARY_VIEW,
                Permission.REPORT_PRICING_EXPORT,

                // *Module 4: Review & Approval — core Reviewer capability
                Permission.REVIEW_VIEW,
                Permission.REVIEW_DECIDE, // + segregation-of-duty check
                Permission.REPORT_AUDIT_TRAIL_EXPORT,

                // *Module 5: Admin & Proposal Analytics — Reviewer gets the
                // dashboard, not User/Category administration (SRS 3.5)
                Permission.ANALYTICS_VIEW,
                Permission.REPORT_ANALYTICS_EXPORT));

        MAP.put(Role.ADMIN, Set.of(
                // Shared reference-data lookups
                Permission.DEPARTMENT_VIEW,
                Permission.OFFERING_VIEW,

                // Admin holds every operational permission across all modules
                // ("full access for support purposes", SRS 2.2), plus the
                // Admin-only User/Category administration of Module 5.
                // *Module 1
                Permission.ACCOUNT_VIEW,
                Permission.ACCOUNT_CREATE,
                Permission.ACCOUNT_EDIT,
                Permission.ACCOUNT_DELETE,
                Permission.PROPOSAL_REQUEST_VIEW,
                Permission.PROPOSAL_REQUEST_CREATE,
                Permission.PROPOSAL_REQUEST_EDIT,
                Permission.PROPOSAL_REQUEST_DELETE,
                Permission.PROPOSAL_REQUEST_REASSIGN,
                Permission.REPORT_ACCOUNT_REQUEST_EXPORT,
                // *Module 2
                Permission.PROPOSAL_VIEW,
                Permission.PROPOSAL_CREATE,
                Permission.PROPOSAL_EDIT,
                Permission.PROPOSAL_SUBMIT,
                Permission.TEMPLATE_VIEW,
                Permission.TEMPLATE_CREATE,
                Permission.TEMPLATE_EDIT,
                Permission.TEMPLATE_DELETE,
                Permission.REPORT_PROPOSAL_EXPORT,
                // *Module 3
                Permission.LINE_ITEM_VIEW,
                Permission.LINE_ITEM_CREATE,
                Permission.LINE_ITEM_EDIT,
                Permission.LINE_ITEM_DELETE,
                Permission.CATALOG_ITEM_VIEW,
                Permission.CATALOG_ITEM_CREATE,
                Permission.CATALOG_ITEM_EDIT,
                Permission.CATALOG_ITEM_DELETE,
                Permission.PRICING_SUMMARY_VIEW,
                Permission.REPORT_PRICING_EXPORT,
                // *Module 4
                Permission.REVIEW_VIEW,
                Permission.REVIEW_DECIDE, // + segregation-of-duty check (applies even to Admin)
                Permission.REPORT_AUDIT_TRAIL_EXPORT,
                // *Module 5 (Admin-exclusive + shared)
                Permission.USER_MANAGE,
                Permission.CATEGORY_VIEW,
                Permission.CATEGORY_CREATE,
                Permission.CATEGORY_EDIT,
                Permission.CATEGORY_DELETE,
                Permission.ANALYTICS_VIEW,
                Permission.REPORT_ANALYTICS_EXPORT));
    }

    public static Set<String> of(Role role) {
        return MAP.getOrDefault(role, Set.of());
    }
}
