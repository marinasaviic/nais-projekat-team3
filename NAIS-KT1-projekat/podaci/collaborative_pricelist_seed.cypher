// --- TeamUsers
MERGE (u1:TeamUser {id: 'user-1'}) SET u1.name='Ana Petrovic', u1.email='ana.petrovic@example.com', u1.position='Manager';
MERGE (u2:TeamUser {id: 'user-2'}) SET u2.name='Marko Jovanovic', u2.email='marko.j@example.com', u2.position='Analyst';
MERGE (u3:TeamUser {id: 'user-3'}) SET u3.name='Ivana Kovac', u3.email='ivana.k@example.com', u3.position='Contributor';
MERGE (u4:TeamUser {id: 'user-4'}) SET u4.name='Petar Nikolic', u4.email='petar.n@example.com', u4.position='Owner';

// --- Teams
MERGE (t1:Team {id: 'team-1'}) SET t1.name='Pricing Team A', t1.type='SUPPORT';
MERGE (t2:Team {id: 'team-2'}) SET t2.name='Pricing Team B', t2.type='ANALYTICS';
MERGE (t3:Team {id: 'team-3'}) SET t3.name='Pricing Team C', t3.type='SALES';

// --- Pricelists
MERGE (p1:Pricelist {id: 'price-1'}) SET p1.name='Retail Pricelist Q1', p1.status='ACTIVE', p1.version='1.0';
MERGE (p2:Pricelist {id: 'price-2'}) SET p2.name='Wholesale Pricelist Q1', p2.status='ACTIVE', p2.version='1.1';
MERGE (p3:Pricelist {id: 'price-3'}) SET p3.name='Promo Pricelist Spring', p3.status='DRAFT', p3.version='0.9';
MERGE (p4:Pricelist {id: 'price-4'}) SET p4.name='Internal Pricelist', p4.status='ARCHIVED', p4.version='2.0';
MERGE (p5:Pricelist {id: 'price-5'}) SET p5.name='Test Pricelist', p5.status='ACTIVE', p5.version='0.1';

// --- Regions
MERGE (r1:Region {id: 'region-1'}) SET r1.name='North', r1.country='RS';
MERGE (r2:Region {id: 'region-2'}) SET r2.name='Central', r2.country='RS';
MERGE (r3:Region {id: 'region-3'}) SET r3.name='South', r3.country='RS';

// --- ActivityLog nodes (MERGE by id to remain idempotent)
MERGE (a1:ActivityLog {id: 'act-1'}) SET a1.actionType='CREATE', a1.timestamp = datetime('2026-04-08T09:00:00'), a1.durationMinutes=30, a1.details='Created pricelist', a1.userId='user-1', a1.pricelistId='price-1';
MERGE (a2:ActivityLog {id: 'act-2'}) SET a2.actionType='UPDATE', a2.timestamp = datetime('2026-04-10T11:15:00'), a2.durationMinutes=20, a2.details='Updated prices', a2.userId='user-2', a2.pricelistId='price-1';
MERGE (a3:ActivityLog {id: 'act-3'}) SET a3.actionType='REVIEW', a3.timestamp = datetime('2026-04-15T14:30:00'), a3.durationMinutes=45, a3.details='Reviewed changes', a3.userId='user-3', a3.pricelistId='price-2';

// --- MEMBER_OF relationships
MATCH (u1:TeamUser {id:'user-1'}), (t1:Team {id:'team-1'})
MERGE (u1)-[m1:MEMBER_OF]->(t1)
SET m1.role='OWNER', m1.assignedAt = datetime() - duration({days: 30});

MATCH (u2:TeamUser {id:'user-2'}), (t1:Team {id:'team-1'})
MERGE (u2)-[m2:MEMBER_OF]->(t1)
SET m2.role='CONTRIBUTOR', m2.assignedAt = datetime() - duration({days: 25});

MATCH (u2:TeamUser {id:'user-2'}), (t2:Team {id:'team-2'})
MERGE (u2)-[m3:MEMBER_OF]->(t2)
SET m3.role='ANALYST', m3.assignedAt = datetime() - duration({days: 20});

MATCH (u3:TeamUser {id:'user-3'}), (t2:Team {id:'team-2'})
MERGE (u3)-[m4:MEMBER_OF]->(t2)
SET m4.role='OWNER', m4.assignedAt = datetime() - duration({days: 18});

MATCH (u4:TeamUser {id:'user-4'}), (t3:Team {id:'team-3'})
MERGE (u4)-[m5:MEMBER_OF]->(t3)
SET m5.role='OWNER', m5.assignedAt = datetime() - duration({days: 12});

// --- WORKS_ON relationships
MATCH (t1:Team {id:'team-1'}), (p1:Pricelist {id:'price-1'})
MERGE (t1)-[w1:WORKS_ON]->(p1)
SET w1.ownershipType='PRIMARY', w1.assignedAt = datetime() - duration({days: 28});

MATCH (t2:Team {id:'team-2'}), (p2:Pricelist {id:'price-2'})
MERGE (t2)-[w2:WORKS_ON]->(p2)
SET w2.ownershipType='PRIMARY', w2.assignedAt = datetime() - duration({days: 22});

MATCH (t3:Team {id:'team-3'}), (p4:Pricelist {id:'price-4'})
MERGE (t3)-[w3:WORKS_ON]->(p4)
SET w3.ownershipType='PRIMARY', w3.assignedAt = datetime() - duration({days: 10});

// --- FOR_REGION relationships
MATCH (p1:Pricelist {id:'price-1'}), (r1:Region {id:'region-1'})
MERGE (p1)-[fr1:FOR_REGION]->(r1)
SET fr1.coverageLevel='HIGH';

MATCH (p2:Pricelist {id:'price-2'}), (r2:Region {id:'region-2'})
MERGE (p2)-[fr2:FOR_REGION]->(r2)
SET fr2.coverageLevel='HIGH';

MATCH (p3:Pricelist {id:'price-3'}), (r3:Region {id:'region-3'})
MERGE (p3)-[fr3:FOR_REGION]->(r3)
SET fr3.coverageLevel='MEDIUM';

// --- PERFORMED relationships linking users to pricelists
MATCH (u1:TeamUser {id:'user-1'}), (p1:Pricelist {id:'price-1'})
MERGE (u1)-[a1:PERFORMED {timestamp: datetime('2026-04-08T09:00:00')}]->(p1)
SET a1.actionType='CREATE', a1.durationMinutes=30;

MATCH (u2:TeamUser {id:'user-2'}), (p1:Pricelist {id:'price-1'})
MERGE (u2)-[a2:PERFORMED {timestamp: datetime('2026-04-10T11:15:00')}]->(p1)
SET a2.actionType='UPDATE', a2.durationMinutes=20;

MATCH (u3:TeamUser {id:'user-3'}), (p2:Pricelist {id:'price-2'})
MERGE (u3)-[a3:PERFORMED {timestamp: datetime('2026-04-15T14:30:00')}]->(p2)
SET a3.actionType='REVIEW', a3.durationMinutes=45;

// Optional: quick verification hints
// MATCH (n) RETURN count(n);
// MATCH (u:TeamUser) RETURN count(u);
// MATCH (t:Team) RETURN count(t);
// MATCH (u:TeamUser)-[r:MEMBER_OF]->(t:Team) RETURN count(r);
// MATCH (t:Team)-[w:WORKS_ON]->(p:Pricelist) RETURN count(w);
// MATCH (p:Pricelist)-[fr:FOR_REGION]->(r:Region) RETURN count(fr);
// MATCH (u:TeamUser)-[a:PERFORMED]->(p:Pricelist) RETURN count(a);
