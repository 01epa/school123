package com.journal.nn.school123.fragment.users;

import android.app.AlertDialog;
import android.content.Context;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.journal.nn.school123.R;
import com.journal.nn.school123.util.LoginUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static com.journal.nn.school123.util.LoginUtil.startEditUserActivity;
import static com.journal.nn.school123.util.LoginUtil.startLoadingActivity;

public class UsersRecyclerViewAdapter extends RecyclerView.Adapter<UsersRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<UserItem> userItems;
    private LayoutInflater layoutInflater;
    private ItemClickListener listener;

    public UsersRecyclerViewAdapter(Context context, List<UserItem> userItems) {
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.userItems = userItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View userFragment = layoutInflater.inflate(R.layout.fragment_user, parent, false);
        return new ViewHolder(userFragment);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserItem userItem = userItems.get(position);
        holder.usernameView.setText(userItem.userName);
        holder.cityView.setText(userItem.city);
        holder.schoolView.setText(userItem.school);

        FloatingActionButton userMenuButton = holder.itemView.findViewById(R.id.menu_user);
        userMenuButton.setOnClickListener(view -> {
            menuButtonHandler(position, view);
        });
    }

    private void menuButtonHandler(int position,
                                   View view) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.user_menu);
        UserItem userItem = userItems.get(position);
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_user_login:
                    startLoadingActivity(context, userItem.userId);
                    return true;
                case R.id.menu_user_edit:
                    startEditUserActivity(context, userItem.userId);
                    return true;
                case R.id.menu_user_remove:
                    new AlertDialog.Builder(context)
                            .setIcon(R.mipmap.ic_launcher)
                            .setTitle(context.getString(R.string.app_name))
                            .setMessage("Вы уверены что хотите удалить ученика - \"" + userItem.userName + "\"")
                            .setPositiveButton("Да", (dialog, which) -> removeUser(position))
                            .setNegativeButton("Нет", null)
                            .show();
                    return true;
            }
            return false;
        });

        try {
            Field field = popupMenu.getClass().getDeclaredField("mPopup");
            field.setAccessible(true);
            MenuPopupHelper menuPopupHelper = (MenuPopupHelper) field.get(popupMenu);
            Method method = MenuPopupHelper.class.getDeclaredMethod("setForceShowIcon", boolean.class);
            method.setAccessible(true);
            method.invoke(menuPopupHelper, true);
        } catch (Exception e) {
            System.out.println("Could not set icons for menu. " + Log.getStackTraceString(e));
        }

        popupMenu.show();
    }

    private void removeUser(int position) {
        UserItem userItem = userItems.remove(position);
        LoginUtil.removeUser(context, userItem.userId);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, userItems.size());
    }

    @Override
    public int getItemCount() {
        return userItems.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView usernameView;
        TextView cityView;
        TextView schoolView;

        ViewHolder(View itemView) {
            super(itemView);
            usernameView = itemView.findViewById(R.id.username);
            cityView = itemView.findViewById(R.id.city);
            schoolView = itemView.findViewById(R.id.school);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                listener.onItemClick(view, getAdapterPosition());
            }
        }
    }

    public UserItem getItem(int id) {
        return userItems.get(id);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.listener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
