package com.weatherService.weatherService.repository;


import com.querydsl.jpa.impl.JPAQueryFactory;
import com.weatherService.weatherService.domian.Address;
import com.weatherService.weatherService.domian.QAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressCrud extends QuerydslRepositorySupport {

    @Autowired
    private AddressRepo addressRepo;

    @Autowired
    private JPAQueryFactory factory;

    public AddressCrud(){
        super(Address.class);
    }

    public int saveAll(List<Address> addressList){
        addressRepo.saveAll(addressList);
        return addressList.size();
    }

    public Address getAddressById(long id){
        QAddress qAddress = QAddress.address;
        return factory.selectFrom(qAddress)
                .where(qAddress.id.eq(id))
                .fetchOne();
    }

    public List<Address> getAddressAll() {
        return addressRepo.findAll();
    }
}
