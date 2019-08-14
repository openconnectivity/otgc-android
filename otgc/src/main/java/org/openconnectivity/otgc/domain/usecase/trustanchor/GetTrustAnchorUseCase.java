/*
 * Copyright 2018 DEKRA Testing and Certification, S.A.U. All Rights Reserved.
 *
 * ****************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *            http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openconnectivity.otgc.domain.usecase.trustanchor;

import io.reactivex.Single;
import org.openconnectivity.otgc.data.repository.IORepository;
import org.openconnectivity.otgc.domain.model.resource.secure.cred.OcCredential;
import org.openconnectivity.otgc.domain.model.resource.secure.cred.OcCredentials;
import org.openconnectivity.otgc.utils.constant.OcfCredUsage;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class GetTrustAnchorUseCase {
    private final IORepository ioRepository;

    @Inject
    public GetTrustAnchorUseCase(IORepository ioRepository) {
        this.ioRepository = ioRepository;
    }

    public Single<List<OcCredential>> execute() {
        return ioRepository.getAssetSvrAsCbor("cred", 0 /* First device */)
                .flatMap(cbor ->  Single.create(emitter -> {
                    OcCredentials credentials = new OcCredentials();
                    credentials.parseCbor(cbor);

                    List<OcCredential> trustAnchorList = new ArrayList<>();
                    for (OcCredential cred : credentials.getCredList()) {
                        if (cred.getCredusage() != null
                                && cred.getCredusage() == OcfCredUsage.OC_CREDUSAGE_TRUSTCA) {
                            trustAnchorList.add(cred);
                        }
                    }

                    emitter.onSuccess(trustAnchorList);
                }));
    }
}
