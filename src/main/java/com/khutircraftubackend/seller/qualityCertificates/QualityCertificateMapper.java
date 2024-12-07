package com.khutircraftubackend.seller.qualityCertificates;

import com.khutircraftubackend.seller.SellerMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Collection;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = SPRING, unmappedTargetPolicy = IGNORE, uses = SellerMapper.class)
public interface QualityCertificateMapper {
	
	QualityCertificateEntity toCertificateEntity(QualityCertificateRequest request);
	void updateCertificateFromRequest(@MappingTarget QualityCertificateEntity certificateEntity, QualityCertificateRequest request);
	
	@Mapping(source = "seller.companyName", target = "sellerCompanyName")
	QualityCertificateResponse toCertificateResponse(QualityCertificateEntity certificateEntity);
	Collection<QualityCertificateResponse> toCertificateResponse(Collection<QualityCertificateEntity> certificateEntities);
}
