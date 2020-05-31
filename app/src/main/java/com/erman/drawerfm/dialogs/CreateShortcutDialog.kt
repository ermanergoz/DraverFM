import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.erman.drawerfm.R
import com.erman.drawerfm.activities.MainActivity

class CreateShortcutDialog : DialogFragment() {
    private lateinit var shortcutName: String
    private lateinit var listener: DialogCreateShortcutListener

    private lateinit var nameEditText: EditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val dialogView: View = inflater.inflate(R.layout.dialog_create_shortcut, null)

            this.nameEditText = dialogView.findViewById(R.id.nameEditText)

            builder.setMessage(R.string.create_shortcut)

                .setPositiveButton(R.string.ok, DialogInterface.OnClickListener { dialog, id ->
                    shortcutName = this.nameEditText.text.toString()
                    listener.dialogCreateShortcutListener(shortcutName, false)
                }).setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { dialog, id ->
                    listener.dialogCreateShortcutListener("", true)
                })

            builder.setView(dialogView)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = context as DialogCreateShortcutListener
        } catch (err: ClassCastException) {
            throw ClassCastException((context.toString() + " must implement DialogCreateShortcutListener"))
        }
    }

    interface DialogCreateShortcutListener {
        fun dialogCreateShortcutListener(shortcutName: String, isCanceled: Boolean)
    }
}