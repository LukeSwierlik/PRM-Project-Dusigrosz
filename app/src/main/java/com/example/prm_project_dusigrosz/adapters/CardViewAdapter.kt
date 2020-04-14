package com.example.prm_project_dusigrosz.adapters

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.drawable.Drawable
import android.provider.BaseColumns
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.prm_project_dusigrosz.R
import com.example.prm_project_dusigrosz.activities.DetailsActivity
import com.example.prm_project_dusigrosz.activities.MainActivity
import com.example.prm_project_dusigrosz.database.TableInfo
import com.example.prm_project_dusigrosz.models.UserViewModel
import com.example.prm_project_dusigrosz.objects.Fields
import com.example.prm_project_dusigrosz.utils.MyViewHolder
import kotlinx.android.synthetic.main.activity_card_view.view.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlin.math.roundToInt

class CardViewAdapter(
    private val mContext: Context,
    private val mDb: SQLiteDatabase,
    private val mUsers: ArrayList<UserViewModel>
): RecyclerView.Adapter<MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cardViewUser = layoutInflater.inflate(R.layout.activity_card_view, parent, false)

        return MyViewHolder(cardViewUser)
    }

    override fun getItemCount(): Int {
        val cursor = mDb.query(
            TableInfo.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null
        )
        val countRow = cursor.count
        cursor.close()

        return countRow
    }

    private fun configTextViewIcon(context: Context, icon: Int, width: Int = 20, height: Int = 20, top: Int = 0, left: Int = 0): Drawable {
        val density = context.resources.displayMetrics.density;
        val drawable = context.resources.getDrawable(icon);

        val widthIcon = (width * density).roundToInt();
        val heightIcon = (height * density).roundToInt();

        drawable.setBounds(left, top, widthIcon, heightIcon);

        return drawable
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val context = holder.itemView.context

        val userCardView = holder.itemView.USER_CARD_VIEW
        val firstName = holder.itemView.FIRST_NAME_TEXT_VIEW_CARD
        val lastName = holder.itemView.LAST_NAME_TEXT_VIEW_CARD
        val phoneNumber = holder.itemView.PHONE_NUMBER_TEXT_VIEW_CARD
        val debt = holder.itemView.DEBT_TEXT_VIEW_CARD

        firstName.text = mUsers[holder.adapterPosition].firstName
        lastName.text = mUsers[holder.adapterPosition].lastName
        phoneNumber.text = mUsers[holder.adapterPosition].phoneNumber
        debt.text = mUsers[holder.adapterPosition].debt.toString()

        val drawableForPhoneNumber = configTextViewIcon(context, android.R.drawable.ic_menu_call)
        phoneNumber.setCompoundDrawables(drawableForPhoneNumber, null, null, null);

        userCardView.setOnClickListener {
            val intentEdit = Intent(context, DetailsActivity::class.java)

            val firstNameEdit = mUsers[holder.adapterPosition].firstName
            val lastNameEdit = mUsers[holder.adapterPosition].lastName
            val phoneNumberEdit = mUsers[holder.adapterPosition].phoneNumber
            val debtEdit = mUsers[holder.adapterPosition].debt.toString()
            val idEdit = mUsers[holder.adapterPosition].id.toString()

            intentEdit.putExtra(Fields.FIRST_NAME, firstNameEdit)
            intentEdit.putExtra(Fields.LAST_NAME, lastNameEdit)
            intentEdit.putExtra(Fields.PHONE_NUMBER, phoneNumberEdit)
            intentEdit.putExtra(Fields.DEBT, debtEdit)
            intentEdit.putExtra(Fields.ID, idEdit)

            context.startActivity(intentEdit)
        }

        userCardView.setOnLongClickListener {
            val builder = AlertDialog.Builder(context)

            builder.setTitle("Removal of the user along with the debt")

            builder.setMessage("Are you sure you want to delete the user and his debt?")

            builder.setPositiveButton("Yes") { _, _ ->
                removeUser(holder.adapterPosition)

                // TODO: A problem with a smooth refresh of the sum of debt on Main activity.
                //  The following solution refreshes all Main activity, which shows a "blink".
//                val sumDebt: Double = mUsers.fold(0.0) { initial, user -> initial + user.debt  }
//                val intentMainActivity = Intent(context, MainActivity::class.java)
//                intentMainActivity.putExtra("sumDebt", sumDebt)
//
//                context.startActivity(intentMainActivity)
            }

            builder.setNegativeButton("No") { _, _ ->
                Toast.makeText(context,"Cancelled.",Toast.LENGTH_SHORT).show()
            }

            val dialog: AlertDialog = builder.create()

            dialog.show()

            true
        }
    }

    private fun removeUser(position: Int) {
        val where = BaseColumns._ID + "=?"
        val whereArgs = arrayOf(mUsers[position].id.toString())

        mDb.delete(TableInfo.TABLE_NAME, where, whereArgs)

        mUsers.removeAt(position)
        notifyItemRemoved(position)

        Toast
            .makeText(mContext, "Removed!", Toast.LENGTH_SHORT)
            .show()
    }
}