package com.debuggeando_ideas.best_travel.infrastructure.abstract_services;

//RQ=Request, RS=Response, ID = Id type
public interface CrudService<RQ, RS, ID> {

    RS create(RQ request);

    RS read(ID id);

    RS update(RQ request, ID id);

    void delete(ID id);
}
