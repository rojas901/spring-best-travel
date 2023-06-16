db.createUser({
        user: 'root',
        pwd: 'toor',
        roles: [
            {
                role: 'readWrite',
                db: 'testDB',
            },
        ],
    });
db.createCollection('app_users', { capped: false });

db.app_users.insert([
    { 
        "username": "ragnar777", 
        "dni": "VIKI771012HMCRG093", 
        "enabled": true, 
        "password": "$2a$10$ydhnIVME.V4s1.sSExZmO.K4OuhSkCBrNuI7K1UDkl3dkkhGrvN1e",
        "role": 
        {
            "granted_authorities": ["read"]
        } 
    },
    { 
        "username": "heisenberg", 
        "dni": "BBMB771012HMCRR022", 
        "enabled": true, 
        "password": "$2a$10$4FX7KNsSwGpSgPV4LJLUWuHIZaT.ywVPCnwsVi47294ht.XV8CTce",
        "role": 
        {
            "granted_authorities": ["read"]
        } 
    },
    { 
        "username": "misterX", 
        "dni": "GOTW771012HMRGR087", 
        "enabled": true, 
        "password": "$2a$10$V7KI7cyJyQ4J8GVBvBaPIORE8vCGQU9INkNsmIzxlYNcwXRYEZVe2",
        "role": 
        {
            "granted_authorities": ["read", "write"]
        } 
    },
    { 
        "username": "neverMore", 
        "dni": "WALA771012HCRGR054", 
        "enabled": true, 
        "password": "$2a$10$woq.HRxSonOKf/jVoTSguOwSelymUzS64qV9CjokkQR2ScZoMXK8S",
        "role": 
        {
            "granted_authorities": ["write"]
        } 
    }
]);