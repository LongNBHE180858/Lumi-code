# Withdraw a course review

```http
POST http://localhost:8080/api/instructor/courses/123/review-requests/withdraw
Authorization: Bearer <instructor-token>
Content-Type: application/json

{"version": 5}
```

Use the latest course ID and version from the builder or instructor list.
Returns `200 OK` with the updated builder aggregate. The course becomes DRAFT,
its version increases, and the existing review request becomes WITHDRAWN.
Withdrawal updates that request; it does not create another request or copy the
course content. A subsequent submission creates a new PENDING request.

Only the owner of a non-deleted PENDING course with a pending review request may
withdraw. Missing/negative version returns 400, stale version or incompatible
state returns 409, and a foreign/missing/deleted course returns 404.
Authentication and active instructor checks also apply.

In the frontend, use **Rút lại gửi duyệt** on a pending course in the management
list or on the builder's Review step, then confirm. The management list reloads;
the builder restores draft editing from the API response.

No schema change is needed for the existing Liquibase schema: request status is
stored as varchar(20) without a status check constraint. WITHDRAWN is added to
the Java and frontend enums; Course status continues to use DRAFT/PENDING.
