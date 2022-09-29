Flyway Scripts
===================

----------

Naming Conventions
------------------
To build a flyway script follow the pattern below:
> - V (for versioned) or R (repeated)
> - YYYYMMDD
> - HHMMSS  (HH must be 24hr based)
> - __ (Double underscore is mandatory)
> - Description of the file
> - .sql

Example:
V20161003214700__Initialize_FundMap_Hibernate_Sequence.sql

Note - 1
------------------

Flyway builds a unique key using `YYYYMMDDHHMMSS`, therefore even
if your file description is different (after the __) the migration
scripts will fail.

Note - 2
------------------

If Flyway migration script fails, the database record for that version
will be flaged as `executed = false` but if you need to rerun the 
script but change the content of the script, the migration will
fail because Flyway makes a checksum of the filecontents and sees
the script is different. Therefore, delete the record from the
database in order to rerun a failed script which was modified.
