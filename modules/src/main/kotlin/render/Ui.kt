package render

import ui.*

class UiGL : Ui() {
    
    override fun flush(): Unit {
//        labelBuffer_.primitievsCount_ = 0;
//        billboardBuffer_.primitievsCount_ = 0;

        _componentsList.clear()
    }
}
