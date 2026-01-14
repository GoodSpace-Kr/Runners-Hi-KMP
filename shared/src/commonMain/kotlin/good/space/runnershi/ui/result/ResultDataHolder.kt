package good.space.runnershi.ui.result

import good.space.runnershi.model.dto.user.UpdatedUserResponse
import good.space.runnershi.ui.running.RunningResultToShow

/**
 * 화면 전환 간 데이터를 안전하게 전달하기 위한 임시 보관소
 */
object ResultDataHolder {
    var userInfo: UpdatedUserResponse? = null
    var runResult: RunningResultToShow? = null

    // 데이터를 사용한 후 메모리에서 해제하기 위한 함수
    fun clear() {
        userInfo = null
        runResult = null
    }
}

