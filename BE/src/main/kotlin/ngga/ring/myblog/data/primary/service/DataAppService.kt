package ngga.ring.myblog.data.primary.service

import org.springframework.stereotype.Service

interface DataAppService{

}

@Service
class DataAppServiceImpl(
  private val tokenAppsService: TokenAppsService
) : DataAppService{

}