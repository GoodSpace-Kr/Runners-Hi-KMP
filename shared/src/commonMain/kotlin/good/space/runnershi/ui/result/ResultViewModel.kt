package good.space.runnershi.ui.result

import androidx.lifecycle.ViewModel

class ResultViewModel : ViewModel() {
    // 홀더에서 데이터를 즉시 가져옴
    val userInfo = ResultDataHolder.userInfo
    val runResult = ResultDataHolder.runResult

    init {
        // 데이터 확인용 로그
        if (runResult == null) {
            println("⚠️ ResultViewModel: 데이터를 찾을 수 없습니다.")
        } else {
            println("✅ ResultViewModel: 데이터 로드 성공")
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Result화면이 완전히 종료(백스택 제거)될 때 메모리 정리
        ResultDataHolder.clear()
        println("🧹 ResultDataHolder: 메모리 정리 완료")
    }
}
