"""
Root pytest conftest.py — extends DYNAMIC_FIELDS to include common timestamp
field variants that should be compared by type only (not exact value) when
validating responses between Source and Destination applications.
"""


def pytest_collection_modifyitems(session, config, items):
    """After test collection, register additional dynamic timestamp fields."""
    patched = False
    for item in items:
        if not patched and hasattr(item, "module") and hasattr(item.module, "DYNAMIC_FIELDS"):
            item.module.DYNAMIC_FIELDS.add("last_updated")
            item.module.DYNAMIC_FIELDS.add("lastUpdated")
            patched = True
