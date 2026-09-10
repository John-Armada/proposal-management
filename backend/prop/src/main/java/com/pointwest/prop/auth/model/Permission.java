package com.pointwest.prop.auth.model;

import lombok.NoArgsConstructor;

/**
 * *Permission string constants for the Proposal Management System.
 *
 * Naming convention: {@code <resource>:<action>}, all lower snake_case,
 * colon-delimited. Values are the strings actually checked in the service
 * layer (e.g. via {@code @PreAuthorize("hasAuthority('proposal:edit')")}).
 * 
 * !IMPORTANT — what these constants do NOT cover:
 * Several SRS business rules are identity-based, not role-based, and cannot
 * be expressed as a static permission grant. Holding a permission below is
 * necessary but not sufficient for these actions; see README.md for the
 * additional runtime checks the service layer must apply:
 * 1. Ownership check on proposal_request:edit/delete, proposal:edit,
 * line_item:create/edit/delete when the actor's role is AUTHOR.
 * 2. Segregation-of-duty check on review:decide (reviewer != author),
 * which applies even to an actor holding the ADMIN role.
 */
@NoArgsConstructor
public final class Permission {

    // ------------------------------------------------------------------
    // *Shared Foundation (SRS 3.0) — reference-data lookups only.
    // Department/Offering have NO write permissions anywhere in this
    // application (SRS 2.7): maintained exclusively via direct DB access.
    // ------------------------------------------------------------------
    public static final String DEPARTMENT_VIEW = "department:view";
    public static final String OFFERING_VIEW = "offering:view";

    // ------------------------------------------------------------------
    // *Module 1: Account & Intake (SRS 3.1 / SDD 3.1)
    // ------------------------------------------------------------------
    public static final String ACCOUNT_VIEW = "account:view";
    public static final String ACCOUNT_CREATE = "account:create";
    public static final String ACCOUNT_EDIT = "account:edit";
    public static final String ACCOUNT_DELETE = "account:delete";

    public static final String PROPOSAL_REQUEST_VIEW = "proposal_request:view";
    public static final String PROPOSAL_REQUEST_CREATE = "proposal_request:create";
    // Ownership check required in addition to this permission — see class javadoc.
    public static final String PROPOSAL_REQUEST_EDIT = "proposal_request:edit";
    public static final String PROPOSAL_REQUEST_DELETE = "proposal_request:delete";
    // "A Proposal Request's Author can be reassigned only by a Reviewer or Admin"
    // (SRS 3.1).
    public static final String PROPOSAL_REQUEST_REASSIGN = "proposal_request:reassign";

    public static final String REPORT_ACCOUNT_REQUEST_EXPORT = "report:account_request:export"; // PROP-INTAKE-6

    // ------------------------------------------------------------------
    // *Module 2: Proposal Authoring & Templates (SRS 3.2 / SDD 3.2)
    // ------------------------------------------------------------------
    public static final String PROPOSAL_VIEW = "proposal:view";
    public static final String PROPOSAL_CREATE = "proposal:create";
    // Ownership check required in addition to this permission — see class javadoc.
    public static final String PROPOSAL_EDIT = "proposal:edit";
    public static final String PROPOSAL_SUBMIT = "proposal:submit"; // owner-only action (SDD 3.4)

    // SDD 3.2 lists role = Author only on every template endpoint (no Reviewer
    // row).
    public static final String TEMPLATE_VIEW = "template:view";
    public static final String TEMPLATE_CREATE = "template:create";
    public static final String TEMPLATE_EDIT = "template:edit";
    public static final String TEMPLATE_DELETE = "template:delete";

    public static final String REPORT_PROPOSAL_EXPORT = "report:proposal:export"; // PROP-DRAFT-6

    // ------------------------------------------------------------------
    // *Module 3: Pricing & Line Items (SRS 3.3 / SDD 3.3)
    // ------------------------------------------------------------------
    // Ownership check required in addition to this permission — see class javadoc.
    public static final String LINE_ITEM_VIEW = "line_item:view";
    public static final String LINE_ITEM_CREATE = "line_item:create";
    public static final String LINE_ITEM_EDIT = "line_item:edit";
    public static final String LINE_ITEM_DELETE = "line_item:delete";

    // Catalog Item is an internal reference list, not a restricted reference
    // table like Department/Offering (SDD 3.3) — Author/Reviewer manage it.
    public static final String CATALOG_ITEM_VIEW = "catalog_item:view";
    public static final String CATALOG_ITEM_CREATE = "catalog_item:create";
    public static final String CATALOG_ITEM_EDIT = "catalog_item:edit";
    public static final String CATALOG_ITEM_DELETE = "catalog_item:delete";

    public static final String PRICING_SUMMARY_VIEW = "pricing_summary:view";
    public static final String REPORT_PRICING_EXPORT = "report:pricing:export"; // PROP-PRICE-6

    // ------------------------------------------------------------------
    // *Module 4: Review & Approval Workflow (SRS 3.4 / SDD 3.4)
    // ------------------------------------------------------------------
    public static final String REVIEW_VIEW = "review:view"; // audit-trail list
    // Segregation-of-duty check required in addition to this permission,
    // even for an actor holding ADMIN — see class javadoc.
    public static final String REVIEW_DECIDE = "review:decide"; // approve/reject

    public static final String REPORT_AUDIT_TRAIL_EXPORT = "report:audit_trail:export"; // PROP-REVIEW-6

    // ------------------------------------------------------------------
    // *Module 5: Admin & Proposal Analytics (SRS 3.5 / SDD 3.5)
    // ------------------------------------------------------------------
    // "Only Admins manage Users, Roles, and Proposal Categories" (SRS 3.5).
    public static final String USER_MANAGE = "user:manage";

    public static final String CATEGORY_VIEW = "category:view";
    public static final String CATEGORY_CREATE = "category:create";
    public static final String CATEGORY_EDIT = "category:edit";
    // Categories in use can only be deactivated, not deleted (SRS 3.5) —
    // that's a business rule enforced in the service, not a separate permission.
    public static final String CATEGORY_DELETE = "category:delete";

    public static final String ANALYTICS_VIEW = "analytics:view";
    public static final String REPORT_ANALYTICS_EXPORT = "report:analytics:export"; // PROP-ADMIN-6
}
