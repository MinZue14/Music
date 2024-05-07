import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.music.R
import com.example.music.Users

class UserAdapter(context: Context, val listUser: ArrayList<Users>) :
    ArrayAdapter<Users>(context, 0, listUser) {

    override fun getCount(): Int {
        return super.getCount()
    }

    override fun getItem(position: Int): Users? {
        return super.getItem(position)
    }

    override fun getItemId(position: Int): Long {
        return listUser[position].userID.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val user = getItem(position)

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.table_user, parent, false)
        }

        val textViewUserID = convertView!!.findViewById<TextView>(R.id.TextViewUserid)
        val textViewUsername = convertView!!.findViewById<TextView>(R.id.TextViewUsername)
        val textViewEmail = convertView.findViewById<TextView>(R.id.TextViewUsermail)
        val textViewPass = convertView.findViewById<TextView>(R.id.TextViewUserpass)

        textViewUserID.text = user?.userID
        textViewUsername.text = user?.username
        textViewEmail.text = user?.email
        textViewPass.text = user?.password

        return convertView


    }
}